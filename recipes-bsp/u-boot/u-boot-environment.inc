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

DEPENDS = "u-boot-mkimage-native"

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
		# Concatenate default environment from u-boot with environment file ${UBOOT_ENV_NAME}.txt
		# In the future we should merge them, i.e. override variable definitions from 
		# default environment with those from the environment file ${UBOOT_ENV_NAME}, so that
		# ${UBOOT_ENV_NAME} would become sort of an environment "fragment"
		envtype=$(get_default_env "${envname}")

		if [ -n "${DEFAULT_ENV}" ]; then
			DEFAULT_ENV_FILE="${DEFAULT_ENV_PATH}/${DEFAULT_ENV}${envtype}.txt"
			if [ -e "$DEFAULT_ENV_FILE" ]; then
				cat "$DEFAULT_ENV_FILE" ${S}/${envname}.txt > ${S}/temp.txt
				mv -f ${S}/temp.txt ${S}/${envname}.txt
			fi
		fi
		# remove any empty lines which might break the environment
		sed '/^[[:space:]]*$/d' -i ${S}/${envname}.txt

		sed -e "s#<machine>#${MACHINE}#" -e "s#<fdt_name>#$(basename `echo ${KERNEL_DEVICETREE} | awk '{ print $1 }'`)#" -e 's#<kernel_name>#${UBOOT_KERNEL_IMAGETYPE}#' -i ${S}/${envname}.txt
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
			mkenvimage ${@base_conditional('SITEINFO_ENDIANNESS', 'le', '', '-b', d)} -s ${envsize} ${UBOOT_ENV_PAD} -o ${S}/${envname}${suffix}.bin ${envname}${srcsuffix}.txt
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

