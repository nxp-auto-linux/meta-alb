URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "d1bf215f68d652892e3c206d1603e55d1dbb940e"
FOLLOWER_RB = "4"
