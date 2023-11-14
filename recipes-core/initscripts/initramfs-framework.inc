#
# Copyright 2023 NXP
#

FILESEXTRAPATHS:prepend := "${THISDIR}/initscripts:"

SRC_URI:append = "\
    file://roothashsign \
"

pkg_postinst:initramfs-module-dmverity() {
    # roothash signing for dm-verity
    install ${WORKDIR}/roothashsign $D/init.d/2-roothashsign
}
