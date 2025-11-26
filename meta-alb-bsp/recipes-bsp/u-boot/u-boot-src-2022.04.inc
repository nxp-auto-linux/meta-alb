URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "439e360bb57dae4030d6237a2c278d0269ab16bf"
FOLLOWER_RB = "4"
