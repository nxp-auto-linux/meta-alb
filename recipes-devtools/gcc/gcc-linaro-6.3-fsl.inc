
FILESEXTRAPATHS_prepend := "${THISDIR}/gcc-${PV}-fsl:"

BASEURI = "git://git.linaro.org/toolchain/gcc;protocol=https;nobranch=1"
SRCREV = "f121e2071eccc565889dd943b3219fbb9349cfcc"

BACKPORTS += "\
           file://CVE-2016-6131.patch \
           file://0001-fix-glibc-2.26-build-with-gcc-6.3.1.patch \
           https://source.codeaurora.org/external/s32ds/compiler/gnu_nxp/plain/gcc.6-3-1-linaro-backport-armv8r-cortex-r52;name=r52;apply=yes \
           https://source.codeaurora.org/external/s32ds/compiler/gnu_nxp/plain/gcc.fix_armv8-libgfortran;name=fortran;apply=yes \
"

SRC_URI[r52.md5sum] = "2e74cc013418f1fc7464af814ffb65f3"
SRC_URI[fortran.md5sum] = "586ff3d397202f3edd9caf85b70f8426"

# Remove support for isl older than 0.15 and fix incorrect isl version hardcoded to 0.15 in gcc
BACKPORTS += "\
           file://0001-2017-02-13-Richard-Biener-rguenther-suse.de.patch \
"

EXTRA_OECONF_BASE_append = " --enable-fix-cortex-a53-835769 --enable-fix-cortex-a53-843419 "

require gcc-common-fsl.inc
