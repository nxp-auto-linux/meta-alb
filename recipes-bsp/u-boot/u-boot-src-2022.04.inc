URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "b5e9112e4bc19ca6018accaf12db1cd9cbcb4b2a"
