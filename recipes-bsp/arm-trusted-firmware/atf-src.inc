URL ?= "git://github.com/nxp-auto-linux/arm-trusted-firmware.git;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV ?= "0cd535d147e915788bad53128078933985ed9b96"
