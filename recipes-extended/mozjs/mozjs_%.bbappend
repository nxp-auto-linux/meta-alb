FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://mozjs-powerpcembedded-fix.patch;patchdir=../../ \
"

