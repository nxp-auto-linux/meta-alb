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

echo "Now we're going to install all the other development packages needed to build Yocto, please wait"

sudo apt-get $UPDATE_FLAG install $PKGS

