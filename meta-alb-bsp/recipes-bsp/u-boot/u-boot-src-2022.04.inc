URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "f1a1c5df75477dc6f9e6f4f80293b6740bf89337"
FOLLOWER_RB = "4"
