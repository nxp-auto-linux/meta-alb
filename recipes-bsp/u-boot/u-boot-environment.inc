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

do_compile () {
	for envname in ${UBOOT_ENV_NAME}; do
		sed -e "s#<machine>#${MACHINE}#" -e "s#<fdt_name>#$(basename `echo ${KERNEL_DEVICETREE} | awk '{ print $1 }'`)#" -e 's#<kernel_name>#${UBOOT_KERNEL_IMAGETYPE}#' -i ${S}/${envname}.txt
		for envsize in ${UBOOT_ENV_IMAGE_SIZE}; do
			if [[ "$envsize" == *:* ]]; then
				suffix="-${envsize%:*}"
				envsize="${envsize#*:}"
				if [ -e "${envname}${suffix}"; then
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
			if [[ "$envsize" == *:* ]]; then
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

