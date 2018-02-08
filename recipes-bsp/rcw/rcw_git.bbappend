FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_append += " \
    file://0001-Updated-rcw.py-to-deal-with-LS2-and-disassembly.patch \
    file://t4bluebox.patch \
    file://0001-Complete-ls2bluebox-RCW-setup-based-on-LS2080ARM-Rev.patch \
    file://0001-rcw-Support-for-LS2084A-in-BB-Classic.patch \
    file://0001-rcw-Basic-support-for-LS2084A-in-BB-Mini.patch \
    file://0001-Turned-PCS-pins-into-GPIO-as-we-do-not-support-SDHC-.patch \
    file://0001-rcw-Added-SDHC-boot-RCW-for-BB-Mini.patch \
    file://0001-rcw-Added-a-PEX3-EP-Gen-2-configuration-for-the-LS20.patch \
    file://0001-rcw-Fixed-blockcopy-implementation-and-disassembly.patch \
    file://0002-Corrected-blockcopy-command-for-fetching-SDHC-U-Boot.patch \
    file://0001-rcw-Broke-awrite-for-older-pbi-format-in-prior-patch.patch \
\
    file://0001-rcw-Updated-rcw.py-script-to-correctly-disassemble-L.patch \
    file://0002-rcw-added-littleendian64b-support-for-LS1012A-into-r.patch \
    file://0001-rcw-Enabled-dont64bswapcrc-to-avoid-swap-on-endianes.patch \
\
    file://0001-rcw-Support-for-LS1012ARDB-added-mirroring-binary-va.patch \
    file://0001-rcw-Added-meta-bluebox-related-boards-to-the-Makefil.patch \
\
    file://0001-rcw-Enabled-proper-RCW-swap-for-functional-QSPI-boot.patch \
    file://0001-rcw-Enabled-A-008851-workaround-for-Gen3-RCW-on-LS10.patch \
    file://0001-rcw-LS2-PCIe-must-be-negotiation-limited-to-Gen2-for.patch \
    file://0001-rcw-Added-BlueBox-PCIe-Gen3-RCWs-to-permit-specific-.patch \
\
    file://0001-rcw-Fixed-IRQxx_BASE-ordering-for-LS2-parts.patch \
\
    file://0001-rcw-rcw.py-disassembler-now-deals-with-littleendian-.patch \
\
    file://0001-rcw-Added-example-conversion-to-rcw.py-for-LS1088ARB.patch \
"

# We want to reuse RCWs without duplication by being able to specify
# a baseline hardware
EXTRA_OEMAKE = "BOARDS=${@d.getVar('MACHINEBASELINE', True).replace('-64b','').replace('-${SITEINFO_ENDIANNESS}','')} DESTDIR=${D}/boot/rcw/"
MACHINEBASELINE ?= "${MACHINE}"
