URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "9ee18d30706d62b9b99ee5d218dad00b9b5d3558"
FOLLOWER_RB = "4"
