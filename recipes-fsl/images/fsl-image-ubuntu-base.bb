# A simple image with a Ubuntu rootfs
LINGUAS_INSTALL = ""
IMAGE_INSTALL = ""
inherit image
export PACKAGE_INSTALL = "${IMAGE_INSTALL}"

inherit nativeaptinstall

APTGET_CHROOT_DIR = "${IMAGE_ROOTFS}"
APTGET_SKIP_UPGRADE = "1"

ROOTFS_POSTPROCESS_COMMAND_append = "do_shell_update; do_update_host; do_update_dhcp_timeout;"

# This must be added first as it provides the foundation for
# subsequent modifications to the rootfs
IMAGE_INSTALL += "\
	ubuntu-base \
	ubuntu-base-dev \
	ubuntu-base-dbg \
	ubuntu-base-doc \
"

# Without the kernel and modules, we can't really use the Linux
IMAGE_INSTALL += "\
	kernel-devicetree \
	kernel-image \
	kernel-modules \
"

# We want to have an itb to boot from in the /boot directory to be flexible
# about U-Boot behavior
TODO_IMAGE_INSTALL += "\
   linux-kernelitb-norootfs-image \
"

APTGET_EXTRA_PACKAGES = "\
	mc \
\
	apt git vim \
	ethtool wget ftp iputils-ping lrzsz \
	net-tools \
"
APTGET_EXTRA_SOURCE_PACKAGES = "\
	iproute2 \
"

USER_SHELL_BASH = "/bin/bash"
USER_PASSWD_BLUEBOX = "SNSRrmx0usMiI"

# Add user bluebox with password bluebox and default shell bash
APTGET_ADD_USERS = "bluebox:${USER_PASSWD_BLUEBOX}:${USER_SHELL_BASH}"

HOST_NAME = "ubuntu-${MACHINE_ARCH}"

DHCP_CONNECTION_TIMEOUT = "30"

##############################################################################
# NOTE: We cannot install arbitrary Yocto packages as they will
# conflict with the content of the prebuilt Ubuntu rootfs and pull
# in dependencies that may break the rootfs.
# Any package addition needs to be carefully evaluated with respect
# to the final image that we build.
##############################################################################

# Set up the network interfaces file only
IMAGE_INSTALL_append += "\
    init-ifupdown \
"

# Support for SJA1105 swich under Linux
IMAGE_INSTALL_append_s32v234bbmini += "\
    sja1105 \
"
# This needs work to enable basic features without pulling in too much
# Support for the S32V CAN interfaces under Linux
#    canutils \
#


do_update_host() {

	set -x

	echo >"${APTGET_CHROOT_DIR}/etc/hostname" "${HOST_NAME}"
	echo >"${APTGET_CHROOT_DIR}/etc/hosts" "127.0.0.1 ${HOST_NAME}"

	set +x
}

do_update_dhcp_timeout() {

	sed -i -E "s/^timeout\s+300/timeout ${DHCP_CONNECTION_TIMEOUT}/g" "${APTGET_CHROOT_DIR}/etc/dhcp/dhclient.conf"
}

IMAGE_ROOTFS_SIZE ?= "8192"
IMAGE_ROOTFS_EXTRA_SPACE_append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "" ,d)}"

COMPATIBLE_MACHINE ="(.*ubuntu)"
