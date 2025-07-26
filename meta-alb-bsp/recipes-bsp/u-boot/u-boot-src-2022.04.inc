URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "36973dfa8d5caafe5b7e6c6b31ada80f73bda4eb"
FOLLOWER_RB = "4"
