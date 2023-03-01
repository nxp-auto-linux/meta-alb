#!/bin/sh

NEW_LIB32_LINUX=""
UPDATE_FLAG=''
if test $force_update; then UPDATE_FLAG='-y --force-yes';fi

# pkgs listed in yocto doc
# https://www.yoctoproject.org/docs/2.4.2/ref-manual/ref-manual.html#intro-requirements
PKGS=" curl gawk wget git-core diffstat unzip texinfo \
     build-essential chrpath socat cpio python2 python3 python3-pip python3-pexpect \
     xz-utils debianutils iputils-ping \
     libsdl1.2-dev xterm libncurses5-dev lz4 zstd"
# pkgs required for fsl use
PKGS="$PKGS make vim-common tofrodos libstring-crc32-perl screen parted \
     dosfstools mtools udev"

if [ "$distro" = "Ubuntu" ]; then
	if [ "$release" = "16.04" -o "$release" = "18.04" ]; then
		# add pks for Ubuntu target: Ubuntu 16.04 or 18.04 toolchain
		PKGS="$PKGS gcc-aarch64-linux-gnu g++-aarch64-linux-gnu libc6-dev-arm64-cross"
	fi
fi

echo "Now we're going to install all the other development packages needed to build Yocto, please wait"

sudo apt-get $UPDATE_FLAG install $PKGS

