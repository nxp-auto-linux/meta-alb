URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "b8cd3232e83d4f3f000d0504c47696e81bb9188e"
FOLLOWER_RB = "3"
