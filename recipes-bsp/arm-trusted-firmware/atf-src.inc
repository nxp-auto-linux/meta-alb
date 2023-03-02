URL ?= "git://github.com/nxp-auto-linux/arm-trusted-firmware.git;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV ?= "9451d7b6c4c4b1bef76f1a0e1f804e5f85d54a40"
