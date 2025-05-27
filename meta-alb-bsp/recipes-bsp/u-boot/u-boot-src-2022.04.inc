URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "369402d0128eeea1e2b252bfbe4d6980cf900b9e"
FOLLOWER_RB = "4"
