URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "3a523306250154aadd708ec1261b2f6584c28c58"
FOLLOWER_RB = "4"
