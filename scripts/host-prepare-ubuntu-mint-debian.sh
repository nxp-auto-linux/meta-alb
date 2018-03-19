#!/bin/sh

NEW_LIB32_LINUX=""
UPDATE_FLAG=''
if test $force_update; then UPDATE_FLAG='-y --force-yes';fi

# pkgs listed in yocto doc
# http://www.yoctoproject.org/docs/current/yocto-project-qs/yocto-project-qs.html#packages
PKGS=" gawk wget git-core diffstat unzip texinfo \
     build-essential chrpath socat libsdl1.2-dev xterm"
# pkgs required for fsl use
PKGS="$PKGS vim-common xz-utils tofrodos libstring-crc32-perl screen"

if [ "$distro" = "Ubuntu" ]; then
	if [ "$release" = "16.04" ]; then
		# add pks for Ubuntu target: Ubuntu 16.04 toolchain
		PKGS="$PKGS gcc-aarch64-linux-gnu g++-aarch64-linux-gnu libc6-dev-arm64-cross"
	fi
fi

echo "Now we're going to install all the other development packages needed to build Yocto, please wait"

sudo apt-get $UPDATE_FLAG install $PKGS

