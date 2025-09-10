URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "d3844c9c2831cb52afe136e8b98d7bd8c7d99c7a"
FOLLOWER_RB = "4"
