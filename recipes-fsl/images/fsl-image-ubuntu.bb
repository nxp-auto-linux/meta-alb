# A more complex image with customer required setup
require fsl-image-ubuntu-base.bb

# Example for use of "ppa:" to install x2go with xfce4
# At this time, this is not available for all versions, so we
# also show how to do a VNC alternative.
APTGET_EXTRA_PPA += '${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "18.04", "ppa:x2go/stable;", \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "20.04", "", \
    "unsupportedubuntuversion" \
    , d) \
    , d)}'
APTGET_EXTRA_PACKAGES += '${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "18.04", "x2goserver x2goserver-xsession", \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "20.04", "x11vnc", \
    "unsupportedubuntuversion" \
    , d) \
    , d)}'
IMAGE_INSTALL_append += '${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "18.04", "", \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "20.04", "x11vnc-init", \
    "unsupportedubuntuversion" \
    , d) \
    , d)}'

APTGET_EXTRA_PACKAGES += "xfce4 xfce4-terminal"

require kernel-source-debian.inc
APTGET_EXTRA_PACKAGES += " \
    libssl-dev \
"

ROOTFS_POSTPROCESS_COMMAND_append = " do_disable_nm_wait_online;"

IMAGE_INSTALL_append_ls2084abbmini += " \
    kvaser \
"

APTGET_EXTRA_PACKAGES += " \
    aptitude \
    gcc g++ cpp \
    build-essential make makedev automake cmake dkms flex bison\
    gdb u-boot-tools device-tree-compiler \
    python-dev \
    zip binutils-dev \
    docker.io \
\
    emacs \
    tmux \
\
    libjson-glib-dev \
    libcurl4-openssl-dev \
    libyaml-cpp-dev \
\
    gstreamer1.0-libav \
    gstreamer1.0-plugins-bad-videoparsers \
    gstreamer1.0-plugins-ugly \
    libgstreamer-plugins-base1.0-dev \
\
    indicator-multiload \
    iperf nginx \
    nmap \
    openssh-server \
\
    sqlitebrowser \
    libsqlite3-dev \
\
    libusb-1.0-0-dev \
\
    libgeos++-dev \
    liblapack-dev \
    libmeschach-dev \
    libproj-dev \
\
    libglademm-2.4-dev \
    libglew-dev \
    libgtkglextmm-x11-1.2-dev \
    libx264-dev \
    freeglut3-dev \
    libraw1394-11 \
    libsdl2-image-dev \
\
    pymacs \
    python-mode \
\
    qgit \
"

# The following packages are apparently not mainstream enough to be
# available for any Ubuntu version. Whoever needs them would have
# to remove the comments appropriately.
#APTGET_EXTRA_PACKAGES += " \
#    python-scipy \
#    python-virtualenv \
#    python-wstool \
#    tilecache \
#    qt4-designer \
#"

# Installing Java is a bit of loaded topic because it is version
# dependent. We default to the Java version based on the Ubuntu version
JAVAVERSION = '${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "18.04", "8", \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "20.04", "11", \
   "unknownjavaversion" \
    , d) \
    , d)}'
JAVALIBPATHSUFFIX = '${@ \
    oe.utils.conditional("JAVAVERSION", "8", "jre/lib/${TRANSLATED_TARGET_ARCH}/jli", \
    oe.utils.conditional("JAVAVERSION", "11", "lib/jli", \
   "unsupportedjavaversion" \
    , d) \
    , d)}'
APTGET_EXTRA_PACKAGES += " \
    openjdk-${JAVAVERSION}-jre \
"
# Instruct QEMU to append (inject) the path to the jdk library to LD_LIBRARY_PATH
# (required by openjdk-${JAVAVERSION}-jdk)
APTGET_EXTRA_LIBRARY_PATH += "/usr/lib/jvm/java-${JAVAVERSION}-openjdk-${UBUNTU_TARGET_ARCH}/${JAVALIBPATHSUFFIX}"

# bluez must not be allowed to (re)start any services, otherwise install will fail
APTGET_EXTRA_PACKAGES_SERVICES_DISABLED += "bluez libbluetooth3 libusb-dev python-bluez avahi-daemon rtkit"

APTGET_SKIP_UPGRADE = "0"

# 2GB of free space to root fs partition (at least 1.5 GB needed during the Bazel build)
IMAGE_ROOTFS_EXTRA_SPACE = "2000000"

COMPATIBLE_MACHINE ="(.*ubuntu)"
