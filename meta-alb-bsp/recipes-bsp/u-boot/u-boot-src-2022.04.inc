URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "7b180b035ad451874c7ec19956cf15442993096b"
FOLLOWER_RB = "4"
