URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "51c1fa8f616ac5ec6be41b9362d02b2bb7a7eb56"
