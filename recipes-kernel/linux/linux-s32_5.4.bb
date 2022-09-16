require recipes-kernel/linux/linux-s32.inc

SRCREV = "64b70fa262905ffced5455e6d346c6b4d951ad25"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

# Perf patches
SRC_URI += " \
    file://perf/0001-perf-cs-etm-Move-definition-of-traceid_list-global-v.patch \
"
