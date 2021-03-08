require recipes-kernel/linux/linux-s32.inc

BRANCH ?= "upstream-5.10-rc5"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV = "e5f5334a1525051f5f3a47c9ebceb3e960c763ae"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
