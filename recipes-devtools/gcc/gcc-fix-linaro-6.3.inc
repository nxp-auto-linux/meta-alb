
FILESEXTRAPATHS_prepend := "${THISDIR}/gcc-fix-${PV}:"

BACKPORTS += "\
           file://CVE-2016-6131.patch \
           file://ubsan-fix-check-empty-string.patch \
"
