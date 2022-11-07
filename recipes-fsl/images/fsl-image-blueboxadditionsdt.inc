#
# This file contains the additions specific to enabling
# ease of use for the Blue Box by providing a graphical Linux
# desktop to use. Hence the "dt" suffix.
#

inherit features_check
REQUIRED_DISTRO_FEATURES += "x11 polkit"

# The various gfx related packages are there to support visualization, though
# some of the selections are quite experimental.

# Ensure lxdm is our display manager and that it is started
IMAGE_INSTALL += " \
    lxdm \
    lxdm-init \
"


# Ensure X is in this installation
IMAGE_INSTALL += " \
    packagegroup-core-x11 \
    xserver-xorg-xvfb \
    xserver-xorg-module-exa \
    xserver-xorg-extension-glx \
"

# Supporting complex evaluation scenarios
IMAGE_INSTALL += " \
    mesa \
    mesa-dev \
"

# Support for remote display usage
IMAGE_INSTALL += " \
    x11vnc \
    x11vnc-init \
    libxinerama \
"

# Add Xfce into the picture
# Xfce tries to start an ssh-agent. That dependency
# is not currently properly reflected, so we install
# the package manually.
# xfce4 wants xrdb without asking for it in the recipe
# Without pam, some things seem to break, too.
IMAGE_INSTALL += " \
    packagegroup-xfce-base \
    xrdb \
    libpam \
    openssh-misc \
"

# For easy visualization of pictures, ristretto can
# be added to the image.
# NOTE: ristretto pulls in tumblerd as dependency.
# tumblerd does an invalid memory access on startup
# which I do not want in a release image. So until
# this is understood, ristretto is not enabled by
# default.
#IMAGE_INSTALL += " \
#    ristretto \
#"

IMAGE_INSTALL += " \
    geany \
"

# Ease of use for the desktop
RDEPENDS:qt4-x11-free:remove = "qmmp"
QT4_PACKAGES = " \
    qt4-x11-free\
    qt4-x11-free-dev\
"

IMAGE_INSTALL += "${@bb.utils.contains("BBFILE_COLLECTIONS", "qt4-layer", "${QT4_PACKAGES}", "", d)}"

# remove some X startup applications which are either inexistent or
# have initialization issues

ROOTFS_AUTOSTART_CLEANUP_LIST ?= "xscreensaver.desktop"
ROOTFS_POSTPROCESS_COMMAND:append = "do_rootfs_startup_cleanup; "

do_rootfs_startup_cleanup() {
    if [ -e "${IMAGE_ROOTFS}/etc/xdg/autostart" ]; then
        cd "${IMAGE_ROOTFS}/etc/xdg/autostart"
        rm -rf ${ROOTFS_AUTOSTART_CLEANUP_LIST}
        cd -
    fi
}

