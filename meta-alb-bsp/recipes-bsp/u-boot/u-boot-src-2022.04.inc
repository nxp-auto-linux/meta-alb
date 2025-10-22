URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "934edeb14b2c7996130487950d6a8b5a5d7bc29f"
FOLLOWER_RB = "4"
