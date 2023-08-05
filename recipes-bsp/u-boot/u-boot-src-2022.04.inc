URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "2c5cf289d58a7691dfd1a32a3f5d769d791a2a3f"
