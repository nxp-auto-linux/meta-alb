URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "1955589cf7ffe962ee83f31c9a91151a8c95586a"
FOLLOWER_RB = "4"
