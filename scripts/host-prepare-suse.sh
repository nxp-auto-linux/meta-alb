#!/bin/sh

UPDATE_FLAG=''
if test $force_update; then UPDATE_FLAG='-y';fi

# pkgs listed in yocto doc
# https://www.yoctoproject.org/docs/2.4.2/ref-manual/ref-manual.html#intro-requirements
PKGS="curl python gcc gcc-c++ git chrpath make wget python-xml \
     diffstat makeinfo python-curses patch socat python3 python3-curses tar python3-pip \
     python3-pexpect xz which libSDL-devel xterm ncurses-devel lz4 zstd"

# pkgs required for fsl iamges
PKGS="$PKGS parted dosfstools mtools udev"

echo "Now we're going to install all the other development packages needed to build Yocto, please wait"

sudo zypper install $UPDATE_FLAG $PKGS
