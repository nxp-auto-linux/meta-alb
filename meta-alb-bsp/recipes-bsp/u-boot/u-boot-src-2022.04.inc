URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "13f761037770a4122e89e64a9ab189204fc603fb"
FOLLOWER_RB = "4"
