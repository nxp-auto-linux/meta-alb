LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION = "5.15.32"

SRC_URI = "git://github.com/nxp-qoriq/linux;protocol=https;nobranch=1"
SRCREV = "fa6c3168595c02bd9d5366fcc28c9e7304947a3d"

require recipes-kernel/linux/linux-qoriq.inc
