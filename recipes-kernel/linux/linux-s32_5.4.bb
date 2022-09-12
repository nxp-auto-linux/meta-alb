require recipes-kernel/linux/linux-s32.inc

SRCREV = "d97a3157dbc319e6ab6fb242478c062bb00d45aa"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

# Perf patches
SRC_URI += " \
    file://perf/0001-perf-cs-etm-Move-definition-of-traceid_list-global-v.patch \
"
