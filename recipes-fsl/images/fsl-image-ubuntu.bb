# A more complex image with customer required setup
require fsl-image-ubuntu-base.bb

require kernel-source-debian.inc

# Add the Bazel tool
IMAGE_INSTALL += "\
    bazel \
"

IMAGE_INSTALL_append_ls2084abbmini += " \
    kvaser \
"

APTGET_EXTRA_PACKAGES += " \
    aptitude \
    openjdk-8-jdk \
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
    libssl-dev \
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
    python-catkin-tools \
    python-mode \
    python-scipy \
    python-virtualenv \
    python-wstool \
    tilecache \
\
    qgit \
    qt4-designer \
 "

# ROS packages go at the end, since there is some 
# Install dependencies for building ROS packages
APTGET_EXTRA_PACKAGES_LAST += " \
    python-rosinstall \
    python-rosinstall-generator \
    python-wstool \
"

# ROS packages, to be installed after ROS setup
APTGET_EXTRA_PACKAGES_LAST += " \
    ros-kinetic-camera-calibration-parsers \
    ros-kinetic-desktop \
    ros-kinetic-geographic-msgs \
    ros-kinetic-gps-common \
    ros-kinetic-joy \
    ros-kinetic-ps3joy \
    ros-kinetic-rosapi \
    ros-kinetic-rosbridge-server \
    ros-kinetic-tf2-web-republisher \
    ros-kinetic-web-video-server \
    ros-kinetic-robot \
"

# Instruct QEMU to append (inject) the path to the jdk library to LD_LIBRARY_PATH
# (required by openjdk-8-jdk)
APTGET_EXTRA_LIBRARY_PATH = "/usr/lib/jvm/java-8-openjdk-arm64/jre/lib/aarch64/jli"

APTGET_EXTRA_PPA = "http://packages.ros.org/ros/ubuntu;hkp://ha.pool.sks-keyservers.net:80;421C365BD9FF1F717815A3895523BAEEB01FA116;deb;ros-latest.list"

# bluez must not be allowed to (re)start any services, otherwise install will fail
APTGET_EXTRA_PACKAGES_SERVICES_DISABLED = "bluez libbluetooth3 libusb-dev python-bluez"

APTGET_SKIP_UPGRADE = "0"

fakeroot do_shell_update() {

    set -x

    # ROS initialization
    chroot "${IMAGE_ROOTFS}" /usr/bin/apt-get -q -y install python-rosdep
    ROS_DEP_BIN=""
    if [ -e "${IMAGE_ROOTFS}/usr/bin/rosdep" ]; then
        ROS_DEP_BIN="/usr/bin/rosdep"
        chroot "${IMAGE_ROOTFS}" $ROS_DEP_BIN init
    fi

    # 'rosdep update' should be run as normal user. Run it as the first user added to this image ('bluebox')
    # Check that we have a non-root user
    FIRST_USER=""
    if [ -n "${APTGET_ADD_USERS}" ]; then
        ALL_USERS="${APTGET_ADD_USERS}"
        FIRST_USER=${ALL_USERS%%:*}

        if [ -z "`cat ${IMAGE_ROOTFS}/etc/passwd | grep $FIRST_USER`" ]; then
            bberror "User $FIRST_USER is invalid."
            FIRST_USER=""
        fi
    fi
    if [ -z "$FIRST_USER" ]; then
        bberror "Ubuntu needs at least one non-root user."
    else

        if [ -n "$ROS_DEP_BIN" ]; then
            HOME=/home/$FIRST_USER chroot --userspec=$FIRST_USER:$FIRST_USER "${IMAGE_ROOTFS}" $ROS_DEP_BIN update
        fi

        # tweak some parts of the filesystem:
        # - change ownership of '/home/bluebox/.ros' to user and group 'bluebox:bluebox'
        # - add user 'bluebox' to group 'docker'
        # do these in a script, do not fail image generation on error
        echo  >"${IMAGE_ROOTFS}/do_update_user.sh" "#!/bin/sh"
        echo >>"${IMAGE_ROOTFS}/do_update_user.sh" "set -x"
        if [ -e "${IMAGE_ROOTFS}/home/$FIRST_USER/.ros" ]; then
            echo >>"${IMAGE_ROOTFS}/do_update_user.sh" "chown $FIRST_USER:$FIRST_USER -R /home/$FIRST_USER/.ros/ || true"
        fi

        if [ -n "`cat ${IMAGE_ROOTFS}/etc/group | grep docker`" ]; then
            echo >>"${IMAGE_ROOTFS}/do_update_user.sh" "usermod -aG docker $FIRST_USER || true"
        fi
        echo >>"${IMAGE_ROOTFS}/do_update_user.sh" "set +x"

        chmod a+x "${IMAGE_ROOTFS}/do_update_user.sh"
        chroot "${IMAGE_ROOTFS}" /bin/bash /do_update_user.sh

        # remove the workaround
        rm -rf "${IMAGE_ROOTFS}/do_update_user.sh"

    fi

    set +x
}

IMAGE_ROOTFS_SIZE ?= "8192"

# 2GB of free space to root fs partition (at least 1.5 GB needed during the Bazel build)
IMAGE_ROOTFS_EXTRA_SPACE = "2000000"

COMPATIBLE_MACHINE ="(.*ubuntu)"
