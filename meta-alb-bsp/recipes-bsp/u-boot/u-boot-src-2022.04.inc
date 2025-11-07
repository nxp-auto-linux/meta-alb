URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "ad54c019b48b907e138b81ec1ab74cb2919679f4"
FOLLOWER_RB = "4"
