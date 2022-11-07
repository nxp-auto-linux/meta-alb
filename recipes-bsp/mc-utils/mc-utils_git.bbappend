FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# BlueBox specific additions/changes
MC_CFG:ls2080abluebox = "ls2080a"
SRC_URI:append:ls2080abluebox = "\
    file://git/config/ls2080a/RDB/custom/ls2080abluebox-dpl-ethbluebox.0x2A_0x41.dts \
    file://git/config/ls2080a/RDB/custom/ls2080abluebox-dpc-0x2a41.dts \
"
MC_CFG:ls2084abluebox = "ls2088a"
SRC_URI:append:ls2084abluebox = "\
    file://git/config/ls2088a/RDB/custom/ls2084abluebox-dpl-ethbluebox.0x2A_0x41.dts \
    file://git/config/ls2088a/RDB/custom/ls2084abluebox-dpc-0x2a41.dts \
"
MC_CFG:ls2084abbmini = "ls2088a"
SRC_URI:append:ls2084abbmini = "\
    file://git/config/ls2088a/RDB/custom/ls2084abbmini-dpl-ethbluebox.0x2A_0x41.dts \
    file://git/config/ls2088a/RDB/custom/ls2084abbmini-dpc-0x2a41.dts \
"
