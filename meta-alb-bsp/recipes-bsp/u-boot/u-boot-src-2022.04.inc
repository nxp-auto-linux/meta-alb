URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "47ac6e7234064bdade0b5748e534f743960b9c11"
FOLLOWER_RB = "4"
