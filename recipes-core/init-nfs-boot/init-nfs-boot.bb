# Copyright 2017-2020 NXP
# Copy local init script in rootfs init

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SUMMARY = "Add custom nfs init script in rootfs"

INIT_SCRIPT ?= "init-nfs-boot"

DEPENDS:append:s32g274abluebox3 = " init-net"

SRC_URI += " \
    file://${INIT_SCRIPT} \
"
FILES:${PN} += "/init"

do_install() {
    install -m 755 ${WORKDIR}/${INIT_SCRIPT} ${D}/${base_prefix}/init
}

RDEPENDS:${PN} += "bash"
