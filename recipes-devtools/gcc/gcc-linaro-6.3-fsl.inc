
FILESEXTRAPATHS_prepend := "${THISDIR}/gcc-${PV}-fsl:"

BASEURI = "git://git.linaro.org/toolchain/gcc;protocol=https;nobranch=1"
SRCREV = "f121e2071eccc565889dd943b3219fbb9349cfcc"

BACKPORTS += "\
           file://CVE-2016-6131.patch \
           file://0001-fix-glibc-2.26-build-with-gcc-6.3.1.patch \
	   https://raw.githubusercontent.com/nxp-auto-tools/s32ds_patches/master/arm_compiler/gnu-nxp-g27a1317/gcc.6-3-1-linaro-backport-armv8r-cortex-r52;name=r52;apply=yes \
	   https://raw.githubusercontent.com/nxp-auto-tools/s32ds_patches/master/arm_compiler/gnu-nxp-g27a1317/gcc.fix_armv8-libgfortran;name=fortran;apply=yes \
"

SRC_URI[r52.md5sum] = "2e74cc013418f1fc7464af814ffb65f3"
SRC_URI[fortran.md5sum] = "586ff3d397202f3edd9caf85b70f8426"

# Remove support for isl older than 0.15 and fix incorrect isl version hardcoded to 0.15 in gcc
BACKPORTS += "\
           file://0001-2017-02-13-Richard-Biener-rguenther-suse.de.patch \
"

require gcc-common-fsl.inc

BB_STRICT_CHECKSUM = "0"

