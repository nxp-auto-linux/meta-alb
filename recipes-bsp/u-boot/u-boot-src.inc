URL ?= "git://source.codeaurora.org/external/autobsps32/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "2502d503069f5e5e6b0a4a184e6932109a3cf605"
