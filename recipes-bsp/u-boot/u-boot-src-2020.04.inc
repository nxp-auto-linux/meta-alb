URL ?= "git://github.com/nxp-auto-linux/u-boot;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "acbccf7de4f6eacd6e5b45c095a67e45dd9a61ef"
