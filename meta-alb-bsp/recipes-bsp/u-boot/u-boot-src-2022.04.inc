URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "b8ec40acbe5e6c6c8f58207eae33c02794d2fcba"
FOLLOWER_RB = "4"
