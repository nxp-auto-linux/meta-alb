URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "c7d01b7e059eb3b704d7a87a3747aba7e77d8ca6"
FOLLOWER_RB = "4"
