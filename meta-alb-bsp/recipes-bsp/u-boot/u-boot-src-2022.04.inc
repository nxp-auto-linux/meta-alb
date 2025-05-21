URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "7bbc174e6eb960f26a931876822ef5beaeb7c4f6"
FOLLOWER_RB = "4"
