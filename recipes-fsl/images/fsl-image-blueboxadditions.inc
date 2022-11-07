#
# This file contains the additions specific to enabling
# ease of use for the BlueBox
#
# <Heinz.Wrobel@nxp.com>
#

inherit extrausers

# We want an easy-to-use standard user "bluebox"
EXTRA_USERS_PARAMS = "\
 useradd -p '' bluebox; \
"

# some packages are required to do native builds of the SDK on the SDK or help in general
# libsdl* are required for native SDK builds
# htop, screen, vim, nano, chkconfig are for convenience
# The Python and cmake additions are to support complex customer evals

# Stuff that may not be in release SDKs, but we want it for the BlueBox
# to be able to control it better
IMAGE_INSTALL += " \
    packagegroup-core-full-cmdline \
"

# We want easy installation of the BlueBox image to the target
# Supported for any Layerscape Gen3 except LX2
DEPLOYSCRIPTS ?= "bbdeployscripts"
DEPLOYSCRIPTS:lx2160a = ""
DEPENDS:append:fsl-lsch3 = " \
    ${DEPLOYSCRIPTS} \
"

# Ease of use additions to the rootfs
IMAGE_INSTALL += " \
    htop \
    screen \
    chkconfig \
    tcpdump \
    vim \
    nano \
    bzip2 \
    minicom \
    cifs-utils \
    socat \
    rsync \
    iperf3 \
    lrzsz \
"

# We want package management on the target for ease of use
IMAGE_FEATURES += "package-management"
IMAGE_INSTALL += " \
    createrepo-c \
    dnf \
"

# Supporting self hosted SDK builds.
# We must have these packages for self hosted builds.
# mtools appears to be required due to Yocto host pollution regarding code page information
# gobject is not strictly required but enables depexp
IMAGE_INSTALL += " \
    libsdl-dev \
    glibc-utils \
    mtools \
"

# Supporting complex evaluation scenarios
IMAGE_INSTALL += " \
    openssl-dev \
    openssl-misc \
    cmake \
    boost \
    boost-dev \
    gsl \
    gsl-dev \
    mesa \
    mesa-dev \
    libproj \
"
# TODO zeus: do not build:
#    libflann \
#    libflann-dev \
#

#    gmock-staticdev \
#    python-pip \
#    libltdl-dev \
#    libltdl \
#

# Supporting complex evaluation scenarios,
# but requires portaudio, which has licensing issues
#IMAGE_INSTALL += " \
#    espeak \
#"

# Supporting complex evaluation scenarios,
# but requires installation of a host side boot jdk
#IMAGE_INSTALL += " \
#    openjdk-8-jdk \
#"

# Supporting complex evaluation scenarios,
# but requires meta-oe/meta-multimedia
#IMAGE_INSTALL += " \
#    libdc1394 \
#    libdc1394-dev \
#"

# Supporting complex evaluation scenarios
# NOTE: To enable opencv, you need to add the word "commercial" to
#       the local.conf entry LICENSE_FLAGS_ACCEPTED. We do not ship
#       elements enabled with commercial requirements by default.
#       In this specific case, "libav" is affected.
#IMAGE_INSTALL += "opencv opencv-apps"
# Opencv headers for development on target
#IMAGE_INSTALL += "opencv-dev opencv-staticdev libopencv-core-dev libopencv-highgui-dev libopencv-imgproc-dev libopencv-objdetect-dev libopencv-ml-dev"
# Boost headers for development on target
IMAGE_INSTALL += "boost-dev"

# Support native builds of Wireshark
#IMAGE_INSTALL += " \
#    libpcap \
#"

# One possible example for ROS integration
#IMAGE_INSTALL += " \
#    packagegroup-ros-world \
#"

# Supporting LTP
IMAGE_INSTALL += " \
    expect \
"

IMAGE_INSTALL:remove = " \
	packagegroup-core-ssh-dropbear \
"

#IMAGE_FEATURES += "splash qt4-pkgs package-management ssh-server-openssh"

