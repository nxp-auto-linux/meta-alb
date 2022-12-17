URL ?= "https://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "23cafa5cfbc03d83f32452822c45cb5bec11e79a"
