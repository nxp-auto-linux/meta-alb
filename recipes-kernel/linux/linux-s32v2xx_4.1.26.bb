require recipes-kernel/linux/linux-s32v2xx.inc

SRC_URI = "git://source.codeaurora.org/external/autobsps32/linux;protocol=https;branch=alb/master"

# If a different repo or revision is needed, use the SRC_URI and SRCREV override mechanism.
# See layer README.FIRST for details.
#SRC_URI = "git://sw-stash.freescale.net/scm/alb/linux.git;protocol=http;branch=develop"
#SRCREV = "${AUTOREV}"

# bsp16.0
SRCREV = "62e61a8b37d163d8dd2fbd0d8331199f8b627074"

# LXC configuration
DELTA_KERNEL_DEFCONFIG_append = "${@base_contains('DISTRO_FEATURES', 'lxc', '${THISDIR}/files/containers_4.1.26.config', '', d)}"
