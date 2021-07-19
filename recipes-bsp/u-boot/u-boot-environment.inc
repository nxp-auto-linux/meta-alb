# This file helps to build a given number of different U-Boot
# environments for a machine. You can have multiple environments for
# a machine because you may have different images requiring different
# environments, e.g., a runtime image, and a factory prep image.
#
# UBOOT_ENV_NAME contains the list of environments to build without
# ".txt" extension per file name. SRC_URI must be preset by the
# recipe properly
inherit siteinfo
inherit deploy

DEPENDS = "u-boot-tools-native coreutils-native"

S = "${WORKDIR}"

# This is just a reasonable assumption about a reasonable max size
# based on the default Digital Networking SDK
UBOOT_ENV_IMAGE_SIZE ?= "8192"

# The default DN SDK flash images pad the environment with 0
UBOOT_ENV_PAD ?= "-p 0"

UBOOT_KERNEL_IMAGETYPE ?= "${KERNEL_IMAGETYPE}"

DEFAULT_ENV ?= ""
DEFAULT_ENV_PATH ?= ""


get_default_env () {
	envname=$1

	# remove whitespaces
	IN="$(echo "${UBOOT_ENV_NAME_MAP}" | sed -e 's/[[:blank:]]//g')"

	set -- "$IN"
	IFS=';'
	for entry in ${IN}; do
		func="${entry%:*}"
		value="${entry#*:}"
		if [ "${func}" = "${envname}" ]; then
			echo "${value}"
			break
		fi
	done
}


do_compile () {
	for envname in ${UBOOT_ENV_NAME}; do
		# We must never ever modify the source file or repeated
		# invocations will work with messed up data!
		# Given that we do not have a separate workdir established,
		# we use the source dir and create local work files
		rm -f ${S}/${envname}.inc
		touch ${S}/${envname}.inc

		# Concatenate default environment from u-boot with
		# environment file ${UBOOT_ENV_NAME}.txt
		# We merge them, i.e. override variable definitions from
		# default environment with those from the environment
		# file ${UBOOT_ENV_NAME}, so that ${UBOOT_ENV_NAME}
		# becomes an environment "fragment"
		envtype=$(get_default_env "${envname}")

		if [ -n "${DEFAULT_ENV}" ]; then
			DEFAULT_ENV_FILE="${DEFAULT_ENV_PATH}/${DEFAULT_ENV}${envtype}.txt"
			if [ -e "$DEFAULT_ENV_FILE" ]; then
				echo "#include \"$DEFAULT_ENV_FILE\"" >>${S}/${envname}.inc
			fi
		fi

		echo "#include \"${S}/${envname}.txt\"" >> ${S}/${envname}.inc

		# Now that we have the list of environment fragments,
		# we preprocess them as a whole into a work file.
		# This concatenates lines properly and leaves things
		# still in the right order.
		# Note how we pass through specific Yocto variables so
		# that we can eventually remove the 'sed' based approach
		# below and use common C preprocessor methods.
		# By using the feature of having $ in identifiers, we
		# make them look more like shell variables for the
		# substitution to avoid conflicts with existing env
		# definitions
		cd "${S}"
		gcc -E -undef -x c -fdollars-in-identifiers -P \
			-D"\$MACHINE=${MACHINE}" \
			-D"\$KERNEL_DEVICETREE=$(basename `echo ${KERNEL_DEVICETREE} | awk '{ print $1 }'`)" \
			-D"\$UBOOT_KERNEL_IMAGETYPE=${UBOOT_KERNEL_IMAGETYPE}" \
			-D"\$TARGET_ARCH=${TARGET_ARCH}" \
			${S}/${envname}.inc >${S}/${envname}.work

		# remove any empty lines which might break the final
		# environment and all leading and trailing spaces
		sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//' -e '/^[[:space:]]*$/d' -i ${S}/${envname}.work

		# Now we want to remove duplicate environment
		# definitions and keep the very last line of such.
		# 1. We add a number prefix to preserve sort order.
		# 2. We reverse sort because we want effectively
		#    the last line retained on unique sorting
		# 3. Unique sorting on only the variable name
		# 4. Sort back based on the number prefix
		# 5. Remove the number prefix
		nl -n rz -w10 -ba -s'=' ${S}/${envname}.work | sort -rnk1 -t'=' | sort -uk2,2 -t'=' | sort -nk1 -t'=' | cut -d'=' -f2- >${S}/${envname}.workunique

		# Finally it's time to generate the environment images
		for envsize in ${UBOOT_ENV_IMAGE_SIZE}; do
			if echo "$envsize" | grep ":"; then
				suffix="-${envsize%:*}"
				envsize="${envsize#*:}"
				if [ -e "${envname}${suffix}" ]; then
					srcsuffix="$suffix"
				else
					srcsuffix=""
				fi
			else
				suffix=""
				srcsuffix=""
			fi
			echo "*** $srcsuffix/$suffix *** $envsize ***"
			mkenvimage ${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', '', '-b', d)} -s ${envsize} ${UBOOT_ENV_PAD} -o ${S}/${envname}${suffix}.bin ${S}/${envname}${srcsuffix}.workunique
		done
	done
}

do_deploy () {
	mkdir -p ${DEPLOYDIR}
	cd ${DEPLOYDIR}
	for envname in ${UBOOT_ENV_NAME}; do
		for envsize in ${UBOOT_ENV_IMAGE_SIZE}; do
			if echo "$envsize" | grep ":"; then
				suffix="-${envsize%:*}"
				envsize="${envsize#*:}"
			else
				suffix=""
			fi
			install ${S}/${envname}${suffix}.bin ${DEPLOYDIR}/${envname}${suffix}-${MACHINE}-${PV}-${PR}.bin

			rm -f ${envname}${suffix}-${MACHINE}.bin
			ln -sf ${envname}${suffix}-${MACHINE}-${PV}-${PR}.bin ${envname}${suffix}-${MACHINE}.bin
		done
	done
}
addtask deploy after do_compile

