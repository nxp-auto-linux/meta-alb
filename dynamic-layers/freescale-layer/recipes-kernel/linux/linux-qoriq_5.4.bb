LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

LINUX_VERSION = "5.4.3"

SRC_URI = "git://source.codeaurora.org/external/qoriq/qoriq-components/linux;nobranch=1 \
    file://0001-Makfefile-linux-5.4-add-warning-cflags-on-LSDK-20.04.patch \
    file://0001-perf-Make-perf-able-to-build-with-latest-libbfd.patch \
"
SRCREV = "134788b16485dd9fa81988681d2365ee38633fa2"

require linux-qoriq.inc
