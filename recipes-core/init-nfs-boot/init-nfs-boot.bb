# Copyright 2017-2018 NXP
# Copy local init script in rootfs init

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SUMMARY = "Add custom nfs init script in rootfs"

inherit insane

SRC_URI += " \
    file://init-nfs-boot \
"
FILES_${PN} += "/init"

do_install() {
    install -m 755 ${WORKDIR}/init-nfs-boot ${D}/${base_prefix}/init
}

# Needed to avoid warning concerning /bin/bash
INSANE_SKIP_${PN} = "file-rdeps"