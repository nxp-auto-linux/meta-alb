URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "0f80a7ef9f9f0e91fedf16d12a4ee24e13c4ac14"
FOLLOWER_RB = "4"
