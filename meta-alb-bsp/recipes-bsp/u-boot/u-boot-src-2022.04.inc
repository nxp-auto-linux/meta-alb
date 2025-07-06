URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "6b3a3c4133dd007f40528991df3317ce93daa7c3"
FOLLOWER_RB = "4"
