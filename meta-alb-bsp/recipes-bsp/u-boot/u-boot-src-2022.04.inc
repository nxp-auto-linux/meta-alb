URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "6a7dc267864419515279eaf1c060f5569099b77e"
FOLLOWER_RB = "4"
