URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "5bed873e96fbac30a72e881caa7be1bbc549b22e"
FOLLOWER_RB = "4"
