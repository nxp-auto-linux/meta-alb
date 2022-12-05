# A more complex image with ROS elements
require fsl-image-ubuntu.bb

# Add the Bazel tool. Maybe we should install it directly from the
# custom apt repository?
IMAGE_INSTALL += "\
    bazel \
"

# ROS packages go at the end, since there is some 
# Install dependencies for building ROS packages
def get_rosversion(d):
    if d.getVar('UBUNTU_TARGET_BASEVERSION', True) == '20.04':
        return 'noetic'
    return 'undefined'
ROS_VERSION = "${@get_rosversion(d)}"

ROS_PPA = "http://packages.ros.org/ros/ubuntu;hkp://keyserver.ubuntu.com:80;C1CF6E31E6BADE8868B172B4F42ED6FBAB17C654;deb;ros-latest.list"

ROS_PACKAGES_KINETIC_MELODIC = " \
    python-catkin-tools \
    python-rosinstall \
    python-rosinstall-generator \
    python-wstool \
"

# ROS packages, to be installed after ROS setup
ROS_PACKAGES_KINETIC_MELODIC += " \
    ros-${ROS_VERSION}-camera-calibration-parsers \
    ros-${ROS_VERSION}-desktop \
    ros-${ROS_VERSION}-geographic-msgs \
    ros-${ROS_VERSION}-gps-common \
    ros-${ROS_VERSION}-joy \
    ros-${ROS_VERSION}-ps3joy \
    ros-${ROS_VERSION}-rosapi \
    ros-${ROS_VERSION}-rosbridge-server \
    ros-${ROS_VERSION}-tf2-web-republisher \
    ros-${ROS_VERSION}-robot \
"

ROS_PACKAGES_NOETIC = "  \
    ros-${ROS_VERSION}-desktop-full \
"

ROS_PACKAGES += "${@bb.utils.contains('ROS_VERSION', 'kinetic', '${ROS_PACKAGES_KINETIC_MELODIC}', '', d)}"
ROS_PACKAGES += "${@bb.utils.contains('ROS_VERSION', 'kinetic', 'ros-${ROS_VERSION}-web-video-server', '', d)}"
ROS_PACKAGES += "${@bb.utils.contains('ROS_VERSION', 'noetic', '${ROS_PACKAGES_NOETIC}', '', d)}"

PYTHON_ROSDEP_PACKAGE = "${@bb.utils.contains('ROS_VERSION', 'kinetic', 'python-rosdep', 'python3-rosdep', d)}"

ADD_ROS_PACKAGES ?= "${ROS_PACKAGES}"
APTGET_EXTRA_PACKAGES_LAST += " \
    ${ADD_ROS_PACKAGES} \
"

APTGET_EXTRA_PPA += "${ROS_PPA}"

fakeroot do_aptget_user_update:append() {

    set -x

    # ROS initialization
    chroot "${IMAGE_ROOTFS}" /usr/bin/apt-get -q -y install "${PYTHON_ROSDEP_PACKAGE}"
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
