URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "d7aff15c32252d27d23cc4e0d322b3315850ab66"
FOLLOWER_RB = "4"
