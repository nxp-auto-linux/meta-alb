URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "46517f7f9367890d4b5ca63a420d3f4218559887"
FOLLOWER_RB = "4"
