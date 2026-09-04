# Copyright 2026 NXP

SUMMARY = "CARV Networking command line client (carv-cli)"
DESCRIPTION = "Standalone Arm64 Linux user-space demo tool that talks to the \
CARV networking co-processor char device to run INSERT/ENUM/LOOKUP/DELETE \
commands on CARV tables. Built from the CARVNET FDK 'carv-cli' sources."
LICENSE = "LA-OPT-NXP-SOFTWARE-LICENSE-V64.2"
LIC_FILES_CHKSUM = "file://license.txt;md5=96b2b32a4c530209b2127d9973875e9a"
NO_GENERIC_LICENSE[LA-OPT-NXP-SOFTWARE-LICENSE-V64.2] = "license.txt"
FILESEXTRAPATHS:prepend := "${NXP_FIRMWARE_LOCAL_DIR}/SW32N7_CARVNET:"

#
# This recipe builds the CARV command line client (carv-cli) from the CARVNET
# installer sources. It expects the content of the CARVNET installer in the
# folder $NXP_FIRMWARE_LOCAL_DIR/SW32N7_CARVNET/ , where the carv-cli sources
# live under carvnet_fdk/carv-cli/ and the license under license.txt .
#
CARVCLI_SRCDIR = "carvnet_fdk/carv-cli"
CARVCLI_LICENSE_FILE = "license.txt"
BB_STRICT_CHECKSUM = "0"

SRC_URI = " \
    ${@f"""file://{d.getVar('CARVCLI_SRCDIR')} """ if os.path.exists(f"""{d.getVar('NXP_FIRMWARE_LOCAL_DIR')}/SW32N7_CARVNET/{d.getVar('CARVCLI_SRCDIR')}""") else ''} \
    ${@f"""file://{d.getVar('CARVCLI_LICENSE_FILE')} """ if os.path.exists(f"""{d.getVar('NXP_FIRMWARE_LOCAL_DIR')}/SW32N7_CARVNET/{d.getVar('CARVCLI_LICENSE_FILE')}""") else ''} \
"

# Both the sources (carvnet_fdk/carv-cli) and license.txt are unpacked directly
# under WORKDIR, so keep S=WORKDIR. LIC_FILES_CHKSUM and NO_GENERIC_LICENSE are
# resolved relative to S, hence the plain 'license.txt' above.
S = "${WORKDIR}"

# Use a dedicated build directory. In the dev (git) flow the bbappend sets
# S=${WORKDIR}/git and CARVCLI_SRCDIR=carv-cli, so a default B (==S) would make
# the '-o ${B}/carv-cli' output collide with the carv-cli source subdirectory.
B = "${WORKDIR}/build"

# The upstream Makefile hard-codes the compiler and omits sysroot/LDFLAGS, so we
# compile directly with the Yocto cross toolchain instead of invoking 'make'.
do_compile() {
    install -d ${B}
    ${CC} ${CFLAGS} ${LDFLAGS} -I${S}/${CARVCLI_SRCDIR} ${S}/${CARVCLI_SRCDIR}/*.c -o ${B}/carv-cli
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/carv-cli ${D}${bindir}/carv-cli
}

FILES:${PN} = "${bindir}/carv-cli"

COMPATIBLE_MACHINE = "s32n"
