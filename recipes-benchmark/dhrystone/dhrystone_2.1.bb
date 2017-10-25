# Copyright 2017 NXP
DESCRIPTION = "DHRYSTONE Benchmark Program"
PR = "r0"
PRIORITY = "optional"
SECTION = "console/utils"
DEPENDS = ""
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README;md5=2178d6495724d4d5a956aef832e06a7c"
AUTHOR = "Reinhold P. Weicker"
# Original shar file: http://www.netlib.org/benchmark/dhry-c
HOMEPAGE = "http://en.wikipedia.org/wiki/Dhrystone"

SRC_URI += "http://fossies.org/linux/privat/old/dhrystone-${PV}.tar.gz;name=dhrystone \
            file://updatesourcetouselibc.patch;pnum=1 \
            "
SRC_URI[dhrystone.md5sum] = "15e13d1d2329571a60c04b2f05920d24"
SRC_URI[dhrystone.sha256sum] = "8c8da46c34fde271b8f60a96a432164d2918706911199f43514861f07ef6b2f1"

S="${WORKDIR}"

inherit deploy

TARGET_OPTIMIZE += "-O4 -DUSE_LIBC"

TARGET_GCCOPTIM += "-g -falign-functions=4 -ffunction-sections -fdata-sections -O3 -march=armv8-a -mtune=cortex-a53   -DGCC -g -falign-jumps=8 -falign-loops=8 -fomit-frame-pointer -fno-inline-functions -fno-inline -mlittle-endian -DUSE_LIBC"

EXTRA_OEMAKE = 'CC="${CC}" GCC="${CC}" OPTIMIZE="${TARGET_OPTIMIZE}" GCCOPTIM="${TARGET_GCCOPTIM}" HZ=100'

do_compile() {
    unset CFLAGS
    oe_runmake
}


do_install() {
    install -d ${D}/home/root/dhrystone
    cp ${WORKDIR}/gcc_dry2 ${D}/home/root/dhrystone
    cp ${WORKDIR}/gcc_dry2reg ${D}/home/root/dhrystone
}
FILES_${PN}-dbg = "/home/root/${PN}/.debug/*"
FILES_${PN}-dbg += "/usr/src/debug/dhrystone/${PV}-${PR}/*"
FILES_${PN} = "/home/root/${PN}/*"

COMPATIBLE_MACHINE = "s32v234evb|s32v234pcie|s32v234tmdp|s32v234bbmini|s32v244sim|s32g275sim|s32r45xsim"
