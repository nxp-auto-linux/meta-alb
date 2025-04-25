URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "0ff5a3dc2932bd8a8803ab58f1dcaed04bada15a"
FOLLOWER_RB = "4"
