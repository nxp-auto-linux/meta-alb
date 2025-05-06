URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "5c61cd23fbe089718245d4c3c71121e65cc7201c"
FOLLOWER_RB = "4"
