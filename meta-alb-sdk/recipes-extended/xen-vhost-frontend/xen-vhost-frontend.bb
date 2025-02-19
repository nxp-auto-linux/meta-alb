SUMMARY = "Xen vhost frontend"
HOMEPAGE = "https://github.com/vireshk/xen-vhost-frontend"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "\
        file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
       "

SRC_URI = "git://github.com/vireshk/xen-vhost-frontend.git;protocol=https;branch=main"
SRCREV = "66ee282dcef4af9fa931f849c4780e5b3b252e0e"

S = "${WORKDIR}/git"

inherit cargo_bin

do_install(){
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/target/aarch64-unknown-linux-gnu/release/xen-vhost-frontend ${D}${bindir}/
}

FILES:${PN} += "${bindir}/xen-vhost-frontend"