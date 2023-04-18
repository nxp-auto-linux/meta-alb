FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# BlueBox specific additions/changes
MC_CFG:ls2084abluebox = "ls2088a"
MC_FLAVOUR:ls2084abluebox = "BLUEBOX"
SRC_URI:append:ls2084abluebox = "\
    file://git/config/ls2088a/LS2088A-${MC_FLAVOUR}/custom/ls2084abluebox-dpl-ethbluebox.0x2A_0x41.dts \
    file://git/config/ls2088a/LS2088A-${MC_FLAVOUR}/custom/ls2084abluebox-dpc-0x2a41.dts \
"
MC_CFG:ls2084abbmini = "ls2088a"
MC_FLAVOUR:ls2084abbmini = "BLUEBOX"
SRC_URI:append:ls2084abbmini = "\
    file://git/config/ls2088a/LS2088A-${MC_FLAVOUR}/custom/ls2084abbmini-dpl-ethbluebox.0x2A_0x41.dts \
    file://git/config/ls2088a/LS2088A-${MC_FLAVOUR}/custom/ls2084abbmini-dpc-0x2a41.dts \
"
MC_CFG:lx2160abluebox3 = "lx2160a"
MC_FLAVOUR:lx2160abluebox3 = "BLUEBOX3"
SRC_URI:append:lx2160abluebox3 = "\
    file://git/config/lx2160a/LX2160A-${MC_FLAVOUR}/custom/lx2160abluebox3-dpl-ethbluebox.31.dts \
    file://git/config/lx2160a/LX2160A-${MC_FLAVOUR}/custom/lx2160abluebox3-dpc-31.dts \
"
MC_CFG:lx2160ardb2bluebox = "lx2160a"
MC_FLAVOUR:lx2160ardb2bluebox = "RDB2BLUEBOX"
SRC_URI:append:lx2160ardb2bluebox = "\
    file://git/config/lx2160a/LX2160A-${MC_FLAVOUR}/custom/lx2160ardb2bluebox-dpl-ethbluebox.19.dts \
    file://git/config/lx2160a/LX2160A-${MC_FLAVOUR}/custom/lx2160ardb2bluebox-dpc-19.dts \
"
