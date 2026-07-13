#!/bin/sh

NEW_LIB32_LINUX=""
UPDATE_FLAG=''
if test $force_update; then UPDATE_FLAG='-y --force-yes';fi

# pkgs listed in yocto doc (Scarthgap 5.0 "Required Packages for the Build Host")
# https://docs.yoctoproject.org/5.0/ref-manual/system-requirements.html
PKGS=" curl gawk wget git-core diffstat unzip texinfo \
     build-essential chrpath socat cpio python3 python3-pip python3-pexpect \
     python3-git python3-jinja2 python3-subunit \
     xz-utils debianutils iputils-ping file libacl1 locales \
     libsdl1.2-dev xterm libncurses5-dev lz4 zstd"
# pkgs required for fsl use
PKGS="$PKGS make vim-common tofrodos libstring-crc32-perl screen parted \
     dosfstools mtools udev gdb"

if [ "$distro" = "Ubuntu" ]; then
	if [ "$release" = "16.04" -o "$release" = "18.04" ]; then
		# add pks for Ubuntu target: Ubuntu 16.04 or 18.04 toolchain
		PKGS="$PKGS gcc-aarch64-linux-gnu g++-aarch64-linux-gnu libc6-dev-arm64-cross"
	fi
fi

echo "Now we're going to install all the other development packages needed to build Yocto, please wait"

sudo apt-get $UPDATE_FLAG install $PKGS

