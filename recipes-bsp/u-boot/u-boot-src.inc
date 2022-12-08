URL ?= "https://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "85e074e1d3a1bd0eb5b14eb53d9cc30eb2d27f97"
