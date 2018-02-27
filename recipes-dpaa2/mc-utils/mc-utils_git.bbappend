FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

# BlueBox specific additions/changes
SRC_URI_append_ls2080abluebox = "\
    file://git/config/ls2080a/RDB/dpl-ethbluebox.0x2A_0x41.dts \
    file://git/config/ls2080a/RDB/dpc-0x2a41.dts \
"
SRC_URI_append_ls2084abluebox = "\
    file://git/config/ls2088a/RDB/dpl-ethbluebox.0x2A_0x41.dts \
    file://git/config/ls2088a/RDB/dpc-0x2a41.dts \
"
SRC_URI_append_ls2084abbmini = "\
    file://git/config/ls2084a/BBMini/dpl-ethbluebox.0x2A_0x41.dts \
    file://git/config/ls2084a/BBMini/dpc-0x2a41.dts \
"

TP_ls2084abbmini  = "BBMini"

REGLEX_ls2080abluebox = "ls2080a"
REGLEX_ls2084abluebox = "ls2088a"
REGLEX_ls2084abbmini  = "ls2084a"

COMPATIBLE_MACHINE_fsl-lsch3 = "(${MACHINE})"

