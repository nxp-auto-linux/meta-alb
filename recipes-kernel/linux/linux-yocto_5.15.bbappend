COMPATIBLE_MACHINE:s32g = "s32g"

SRC_URI_GITHUB = "git://github.com/nxp-auto-linux/linux.git;name=machine;protocol=https"
SRCBRANCH_GITHUB = "release/bsp37.0-5.15.96-rt"
SRCREV_GITHUB = "f2b25660adcf5713afcd24abdae0dbe69b199701"

SRCBRANCH:s32g = "${SRCBRANCH_GITHUB}"
SRCREV_machine:s32g = "${SRCREV_GITHUB}"

SRC_URI:remove:s32g = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH};"
SRC_URI:append:s32g = " \
	${SRC_URI_GITHUB};branch=${SRCBRANCH}"

KMACHINE:s32g = "s32g"
