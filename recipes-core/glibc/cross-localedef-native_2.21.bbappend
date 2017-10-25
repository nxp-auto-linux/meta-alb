FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

# We need this patch to allow host side aarch64
SRC_URI += "\
    file://enable-host-aarch64.patch \
"

