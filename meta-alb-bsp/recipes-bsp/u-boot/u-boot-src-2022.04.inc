URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ??= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "b0ba0ebdfac547b57a45acf5478a23b6ec2af585"
FOLLOWER_RB = "4"
