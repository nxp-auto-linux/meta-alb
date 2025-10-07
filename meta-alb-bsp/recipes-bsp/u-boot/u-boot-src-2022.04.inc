URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "8877488dd33b9b6f303eba7ec51fb71a156754e7"
FOLLOWER_RB = "4"
