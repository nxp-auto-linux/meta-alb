# A more complex image with ROS elements
require fsl-image-ubuntu.bb

# Install dependencies for building ROS packages.
#
# The ROS distribution is selected from the Ubuntu base version:
#   - Ubuntu 24.04 (noble) -> ROS 2 Jazzy (packages.ros.org/ros2)
# ROS 2 Jazzy only targets noble; this layer's other supported base
# (22.04/jammy) has no ROS 2 support implemented here, so warn instead
# of silently installing nothing.
def get_rosversion(d):
    baseversion = d.getVar('UBUNTU_TARGET_BASEVERSION')
    if baseversion == '24.04':
        return 'jazzy'
    bb.warn("ROS 2 is not supported for Ubuntu %s; no ROS packages will be installed." % baseversion)
    return 'undefined'
ROS_VERSION = "${@get_rosversion(d)}"

# ROS 2 is published from the ros2/ubuntu apt archive, signed with the
# Open Robotics key (C1CF6E31E6BADE8868B172B4F42ED6FBAB17C654).
ROS_PPA_JAZZY = "http://packages.ros.org/ros2/ubuntu;hkp://keyserver.ubuntu.com:80;C1CF6E31E6BADE8868B172B4F42ED6FBAB17C654;deb;ros2-latest.list"

ROS_PPA = ""
ROS_PPA += "${@bb.utils.contains('ROS_VERSION', 'jazzy', '${ROS_PPA_JAZZY}', '', d)}"

# ros-jazzy-desktop-full pulls in the Gazebo simulation stack (+213
# packages, mostly 'universe'); to keep the image lean, we ship the
# lighter 'desktop' variant (ros-base + visualization/demo tooling)
# instead.
ROS_PACKAGES_JAZZY = " \
    ros-${ROS_VERSION}-desktop \
"

ROS_PACKAGES += "${@bb.utils.contains('ROS_VERSION', 'jazzy', '${ROS_PACKAGES_JAZZY}', '', d)}"

PYTHON_ROSDEP_PACKAGE = "python3-rosdep"

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
