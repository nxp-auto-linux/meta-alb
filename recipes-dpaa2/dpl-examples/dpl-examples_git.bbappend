FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

# BlueBox specific additions/changes
SRC_URI_append_ls2080abluebox = "\
    file://git/ls2080a/RDB/dpl-ethbluebox.0x2A_0x41.dts \
    file://git/ls2080a/RDB/dpc-0x2a41.dts \
"
SRC_URI_append_ls2084abluebox = "\
    file://git/ls2088a/RDB/dpl-ethbluebox.0x2A_0x41.dts \
    file://git/ls2088a/RDB/dpc-0x2a41.dts \
"
SRC_URI_append_ls2084abbmini = "\
    file://git/ls2084a/BBMini/dpl-ethbluebox.0x2A_0x41.dts \
    file://git/ls2084a/BBMini/dpc-0x2a41.dts \
"

# We use aliasing to a known baseline rather than duplication
TP ?= "RDB"
TP_ls2084abbmini  = "BBMini"

REGLEX_ls2080abluebox = "ls2080a"
REGLEX_ls2084abluebox = "ls2088a"
REGLEX_ls2084abbmini  = "ls2084a"

do_install () {
    install -d "${D}/boot"
    install -m 644 ${S}/${REGLEX}/${TP}/*.dtb "${D}/boot"
    CUSTOM=`echo ${S}/${REGLEX}/${TP}/custom/*.dtb`
    if [ "${CUSTOM}" != "${S}/${REGLEX}/${TP}/custom/*.dtb" ]; then
        install -m 644 ${S}/${REGLEX}/${TP}/custom/*.dtb "${D}/boot"
    fi
}

do_deploy () {
    install -d "${DEPLOYDIR}/dpl-examples"
    install -m 644 ${S}/${REGLEX}/${TP}/*.dtb "${DEPLOYDIR}/dpl-examples"
    CUSTOM=`echo ${S}/${REGLEX}/${TP}/custom/*.dtb`
    if [ "${CUSTOM}" != "${S}/${REGLEX}/${TP}/custom/*.dtb" ]; then
        install -m 644 ${S}/${REGLEX}/${TP}/custom/*.dtb "${DEPLOYDIR}/dpl-examples"
    fi
}


COMPATIBLE_MACHINE_fsl-lsch3 = "(${MACHINE})"

