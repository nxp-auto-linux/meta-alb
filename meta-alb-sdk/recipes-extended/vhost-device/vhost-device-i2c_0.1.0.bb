SUMMARY = "vhost i2c backend device"
DESCRIPTION = "A vhost-user backend that emulates a VirtIO I2C device"
HOMEPAGE = "https://github.com/rust-vmm/vhost-device"
LICENSE = "Apache-2.0 | BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://LICENSE-APACHE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://LICENSE-BSD-3-Clause;md5=2489db1359f496fff34bd393df63947e \
"

SRC_URI += "crate://crates.io/vhost-device-i2c/0.1.0"
SRC_URI[vhost-device-i2c-0.1.0.sha256sum] = "a77923a4f161887ca2a19ead2d2f2271d62d1268e774265c42c277367f019a3f"

inherit cargo
inherit cargo-update-recipe-crates

include vhost-device-i2c-crates.inc
