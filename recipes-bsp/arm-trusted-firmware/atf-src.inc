URL ?= "git://github.com/nxp-auto-linux/arm-trusted-firmware.git;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV ?= "1d6ff65b8fd1be6efbd2a9d52ffe7dd8af24a653"
