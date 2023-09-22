URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "a1f723b25079f1ca3e09d8cf0d021b148a7b220a"
