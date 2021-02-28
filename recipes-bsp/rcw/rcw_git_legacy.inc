
LIC_FILES_CHKSUM = "file://LICENSE;md5=45a017ee5f4cfe64b1cddf2eb06cffc7"

DEPENDS += "change-file-endianess-native"
SRCREV = "baa4117294631db9e6ac2baa03ed5bdb793d7a81"

SRC_URI_append += " \
    file://0001-Complete-ls2bluebox-RCW-setup-based-on-LS2080ARM-Rev.patch \
    file://0001-rcw-Support-for-LS2084A-in-BB-Classic.patch \
    file://0001-rcw-Basic-support-for-LS2084A-in-BB-Mini.patch \
    file://0001-Turned-PCS-pins-into-GPIO-as-we-do-not-support-SDHC-.patch \
    file://0001-rcw-Added-SDHC-boot-RCW-for-BB-Mini.patch \
    file://0001-rcw-Added-a-PEX3-EP-Gen-2-configuration-for-the-LS20.patch \
    file://0002-Corrected-blockcopy-command-for-fetching-SDHC-U-Boot.patch \
\
\
    file://0001-rcw-Added-meta-bluebox-related-boards-to-the-Makefil.patch \
\
    file://0001-rcw-Enabled-proper-RCW-swap-for-functional-QSPI-boot.patch \
    file://0001-rcw-Enabled-A-008851-workaround-for-Gen3-RCW-on-LS10.patch \
    file://0001-rcw-LS2-PCIe-must-be-negotiation-limited-to-Gen2-for.patch \
    file://0001-rcw-Added-BlueBox-PCIe-Gen3-RCWs-to-permit-specific-.patch \
\
    file://0001-rcw-Fixed-IRQxx_BASE-ordering-for-LS2-parts.patch \
\
    file://0001-rcw-Added-a-PEX3-EP-Gen-2-configuration-for-BBMini.patch \
\
\
"

export PYTHON = "${USRBINPATH}/python2"

# We want to reuse RCWs without duplication by being able to specify
# a baseline hardware
EXTRA_OEMAKE = "BOARDS=${@d.getVar('MACHINEBASELINE', True).replace('-64b','').replace('-${SITEINFO_ENDIANNESS}','')} DESTDIR=${D}/boot/rcw/"
MACHINEBASELINE ?= "${MACHINE}"
MACHINEBASELINE_ls1043avnpevb ?= "ls1043ardb"

# We need some aliases for the BB image on standard eval boards to
# avoid duplicating their RCW setups needlessly.
M_ls1043abluebox = "ls1043ardb"
M_ls1046abluebox = "ls1046ardb"

do_install () {
    if [ -f ${S}/${M}/Makefile ]; then
        oe_runmake BOARDS=${M} DESTDIR=${D}/boot/rcw/ install
        if [ ${M} = ls2088ardb ]; then
            oe_runmake BOARDS=${M}_rev1.1  DESTDIR=${D}/boot/rcw/ install
        fi
    else
        install -d ${D}/boot/rcw
        cp -a ${S}/${M} ${D}/boot/rcw/
        chown -R root:root ${D}
    fi
    for f in `find ${D}/boot/rcw/ -name "*qspiboot*"`;do
        if echo $f |grep -q "qspiboot_sben"; then
            continue
        fi
        f_swap=`echo $f |sed -e 's/qspiboot/qspiboot_swap/'`
        tclsh ${STAGING_BINDIR_NATIVE}/byte_swap.tcl $f $f_swap 8
        mv -f $f_swap $f
    done
}
