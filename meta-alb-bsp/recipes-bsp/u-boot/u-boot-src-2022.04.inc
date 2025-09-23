URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "be167c1e6b521a44a5f6db9fb153f5093b5f735c"
FOLLOWER_RB = "4"
