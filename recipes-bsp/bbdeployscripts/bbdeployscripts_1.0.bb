# Deployment of the default images manually is annoying.
# This recipe creates proper scripts to help deployment
# Heinz.Wrobel@nxp.com
#
SUMMARY = "Create scripts for Blue Box image deployment"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

PV = "1.0+fslgit"

inherit deploy

DEPENDS = "u-boot-mkimage-native dtc-native"

UBOOT_DEPLOYSCRIPT_NAME_ITS ?= "bbdeployimage"
LINUX_DEPLOYSCRIPT_NAME_SH ?= "bbdeployimage"
LINUX_REPLACESCRIPT_NAME_SH ?= "bbreplacerootfs"
UBOOT_DEPLOYSCRIPT_NAME:prepend:ls2084abluebox ?= "ls2deployimage "
UBOOT_DEPLOYSCRIPT_NAME:prepend:ls2084abbmini  ?= "ls2deployimage ls2factoryprep "
UBOOT_DEPLOYSCRIPT_NAME:prepend:ls1043abluebox ?= "ls1nordeployimage "
UBOOT_DEPLOYSCRIPT_NAME:prepend:ls1046abluebox ?= "ls1nordeployimage "
UBOOT_DEPLOYSCRIPT_NAME:prepend:s32g274abluebox3 ?= "s32gqspideploy \
	s32gemmcdeploy \
	s32grcwdeploy \
	"

SRC_URI = "file://${UBOOT_DEPLOYSCRIPT_NAME_ITS}.its \
           file://${LINUX_DEPLOYSCRIPT_NAME_SH}.sh \
           file://${LINUX_REPLACESCRIPT_NAME_SH}.sh \
           file://ls1nordeployimage.txt \
           file://ls2deployimage.txt \
           file://ls2factoryprep.txt \
           file://s32gqspideploy.txt \
           file://s32gemmcdeploy.txt \
           file://s32grcwdeploy.txt \
           file://s32gdeployall.txt \
"

S = "${WORKDIR}"

do_compile () {
        # Legacy script image creation (for is a trick to avoid spaces while still allowing external override due to prepend above)
        # The last one takes hold
        for item in ${UBOOT_DEPLOYSCRIPT_NAME}; do
                mkimage -T script -C none -n '${UBOOT_DEPLOYSCRIPT_NAME_ITS}' -d ${S}/${item}.txt ${WORKDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}.img 
        done

        # ITB image creation
        mkimage -f ${UBOOT_DEPLOYSCRIPT_NAME_ITS}.its ${WORKDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}.itb
}

do_deploy () {
        mkdir -p ${DEPLOYDIR}
        cd ${DEPLOYDIR}

        # Note that the itb is NOT using MACHINE. It contains all the machine specific scripts and is therefore universal.
        if  [ -e "${WORKDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}.itb" ]; then
                install ${WORKDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}.itb ${DEPLOYDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}-${PV}-${PR}.itb

                rm -f ${UBOOT_DEPLOYSCRIPT_NAME_ITS}.itb
                ln -sf ${UBOOT_DEPLOYSCRIPT_NAME_ITS}-${PV}-${PR}.itb ${DEPLOYDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}.itb
        fi

        if  [ -e "${WORKDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}.img" ]; then
                install ${WORKDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}.img ${DEPLOYDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}-${MACHINE}-${PV}-${PR}.img

                rm -f ${DEPLOYDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}-${MACHINE}.img
                ln -sf ${UBOOT_DEPLOYSCRIPT_NAME_ITS}-${MACHINE}-${PV}-${PR}.img ${DEPLOYDIR}/${UBOOT_DEPLOYSCRIPT_NAME_ITS}-${MACHINE}.img
        fi

        # Install universal shell scripts
        for item in "${LINUX_DEPLOYSCRIPT_NAME_SH}" "${LINUX_REPLACESCRIPT_NAME_SH}"; do
                if  [ -e "${S}/${item}.sh" ]; then
                        install ${S}/${item}.sh ${DEPLOYDIR}/${item}-${MACHINE}-${PV}-${PR}.sh

                        rm -f ${DEPLOYDIR}/${item}.sh
                        ln -sf ${item}-${MACHINE}-${PV}-${PR}.sh ${DEPLOYDIR}/${item}.sh
                fi
        done

}
addtask deploy after do_compile

