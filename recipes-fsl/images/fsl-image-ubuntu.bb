# A more complex image with customer required setup
require fsl-image-ubuntu-base.bb

require kernel-source-debian.inc

# Example for use of "ppa:" to install x2go with xfce4
APTGET_EXTRA_PPA += "ppa:x2go/stable;"
APTGET_EXTRA_PACKAGES += "xfce4 xfce4-terminal"
APTGET_EXTRA_PACKAGES += "x2goserver x2goserver-xsession"

ROOTFS_POSTPROCESS_COMMAND_append = " do_disable_nm_wait_online;"

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
    python-mode \
    python-scipy \
    python-virtualenv \
    python-wstool \
    tilecache \
\
    qgit \
    qt4-designer \
 "

# Instruct QEMU to append (inject) the path to the jdk library to LD_LIBRARY_PATH
# (required by openjdk-8-jdk)
APTGET_EXTRA_LIBRARY_PATH += "/usr/lib/jvm/java-8-openjdk-${DEBIAN_TARGET_ARCH}/jre/lib/${TRANSLATED_TARGET_ARCH}/jli"

# bluez must not be allowed to (re)start any services, otherwise install will fail
APTGET_EXTRA_PACKAGES_SERVICES_DISABLED += "bluez libbluetooth3 libusb-dev python-bluez avahi-daemon rtkit"

APTGET_SKIP_UPGRADE = "0"

fakeroot do_disable_nm_wait_online() {
	set -x

	# In xenial, not in bionic, we want to mask NetworkManager-wait-online service
	# as it runs: '/usr/bin/nm-online -s -q --timeout=30', which fails at boot time,
	# adding a delay of 'timeout' seconds, although the network interfaces are
	# working properly.
	if [ "${UBUNTU_TARGET_BASEVERSION}" = "16.04" ]; then
		ln -sf "/dev/null" "${APTGET_CHROOT_DIR}/etc/systemd/system/NetworkManager-wait-online.service"
	fi

	set +x
}

# 2GB of free space to root fs partition (at least 1.5 GB needed during the Bazel build)
IMAGE_ROOTFS_EXTRA_SPACE = "2000000"

COMPATIBLE_MACHINE ="(.*ubuntu)"
