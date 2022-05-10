URL ?= "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "fda3b36958e6cdc5e79caa9c97b4631547751e22"
