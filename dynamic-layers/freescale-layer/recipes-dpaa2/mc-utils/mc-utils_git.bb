SUMMARY = "Datapath layout examples"
SECTION = "dpaa2"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=386a6287daa6504b7e7e5014ddfb3987"

DEPENDS = "dtc-native"

inherit deploy

SRC_URI = "git://source.codeaurora.org/external/qoriq/qoriq-components/mc-utils.git;nobranch=1"
SRCREV = "b79fe4b47364dfd2fe263f701ad6b818a75b036b"

# We use aliasing to a known baseline rather than duplication
TP ?= "RDB"
S = "${WORKDIR}/git"

REGLEX_ls2084a = "ls2088a"
REGLEX_ls2088a = "ls2088a"
REGLEX_ls1088a = "ls1088a"

do_compile_prepend () {
    cd config
    base_do_compile
    cd ..
}

do_install () {
    install -d ${D}/boot
    install -m 644 ${S}/config/${REGLEX}/${TP}/*.dtb ${D}/boot
    CUSTOM=`echo ${S}/${REGLEX}/${TP}/custom/*.dtb`
    if [ "${CUSTOM}" != "${S}/${REGLEX}/${TP}/custom/*.dtb" ]; then
        install -m 644 ${S}/config/${REGLEX}/${TP}/custom/*.dtb ${D}/boot
    fi
}

do_deploy () {
    install -d ${DEPLOYDIR}/mc-utils/config
    install -m 644 ${S}/config/${REGLEX}/${TP}/*.dtb ${DEPLOYDIR}/mc-utils/config
    CUSTOM=`echo ${S}/${REGLEX}/${TP}/custom/*.dtb`
    if [ "${CUSTOM}" != "${S}/${REGLEX}/${TP}/custom/*.dtb" ]; then
        install -m 644 ${S}/config/${REGLEX}/${TP}/custom/*.dtb ${DEPLOYDIR}/mc-utils/config
    fi
}
addtask deploy before do_build after do_install

PACKAGES += "${PN}-image"
FILES_${PN}-image += "/boot"

COMPATIBLE_MACHINE = "(ls2080ardb|ls2080a|ls2084a|ls2088a|ls1088a)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

