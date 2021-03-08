require recipes-kernel/linux/linux-s32.inc

BRANCH ?= "${RELEASE_BASE}-${PV}-rt"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV = "03141fd4dbdc01c5a12a8908ddf40690e852a1ba"

# Temporary override 'LIC_FILES_CHKSUM' variable until
# we officially upgrade to Linux Kernel 4.19
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"
