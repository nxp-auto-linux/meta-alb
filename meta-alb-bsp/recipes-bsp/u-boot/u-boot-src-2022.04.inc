URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "bb40bf4f50abfefea3217cf1f5bd67448e9d62f8"
FOLLOWER_RB = "4"
