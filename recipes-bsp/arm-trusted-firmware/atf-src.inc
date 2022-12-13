URL ?= "https://github.com/nxp-auto-linux/arm-trusted-firmware.git;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV ?= "66179fbc8f0ceb798cfe0daf51cb947c34f259e1"
