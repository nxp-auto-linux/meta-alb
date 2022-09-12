require recipes-kernel/linux/linux-s32.inc

SRCREV = "65d0762b4350f9b6a359f0b92e0c247f78d9a04d"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

# Perf patches
SRC_URI += " \
    file://perf/0001-perf-cs-etm-Move-definition-of-traceid_list-global-v.patch \
"
