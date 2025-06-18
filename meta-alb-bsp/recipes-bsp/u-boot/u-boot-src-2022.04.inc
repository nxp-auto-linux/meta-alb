URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "5b59076b418f90d46a5bebfd7164e834ba739000"
FOLLOWER_RB = "4"
