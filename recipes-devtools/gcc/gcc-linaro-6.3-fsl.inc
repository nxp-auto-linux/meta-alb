
FILESEXTRAPATHS_prepend := "${THISDIR}/gcc-${PV}-fsl:"

BASEURI = "git://git.linaro.org/toolchain/gcc;protocol=https;nobranch=1"
SRCREV = "f121e2071eccc565889dd943b3219fbb9349cfcc"

BACKPORTS += "\
           file://CVE-2016-6131.patch \
           https://source.codeaurora.org/external/s32ds/compiler/gnu_nxp/plain/gcc.6-3-1-linaro-backport-armv8r-cortex-r52;name=r52;apply=yes \
           https://source.codeaurora.org/external/s32ds/compiler/gnu_nxp/plain/gcc.fix_armv8-libgfortran;name=fortran;apply=yes \
"

SRC_URI[r52.md5sum] = "2e74cc013418f1fc7464af814ffb65f3"
SRC_URI[fortran.md5sum] = "586ff3d397202f3edd9caf85b70f8426"
