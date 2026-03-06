URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "05345f426559934baeb810f3a7bb00936e7ddf1c"
FOLLOWER_RB = "4"
