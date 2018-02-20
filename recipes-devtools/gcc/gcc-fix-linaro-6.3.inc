
FILESEXTRAPATHS_prepend := "${THISDIR}/gcc-fix-${PV}:"

BASEURI = "git://source.codeaurora.org/external/s32ds/compiler/gcc;protocol=https;branch=NXP_patches"
SRCREV = "2bb6acb89f8fc84fc10619672998778e2938e078"

BACKPORTS += "\
           file://CVE-2016-6131.patch \
"
