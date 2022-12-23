URL ?= "git://github.com/nxp-auto-linux/arm-trusted-firmware.git;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV ?= "8e6887f498844ea7f9823ca701fb388d58b93934"
