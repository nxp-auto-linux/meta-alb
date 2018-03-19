#!/bin/sh

UPDATE_FLAG=''
if test $force_update; then UPDATE_FLAG='-y';fi

# pkgs listed in yocto doc
# http://www.yoctoproject.org/docs/current/yocto-project-qs/yocto-project-qs.html#packages
PKGS="python gcc gcc-c++ libtool \
     subversion git chrpath automake make wget \
     diffstat makeinfo freeglut-devel libSDL-devel"
# pkgs required for fsl use
PKGS="patch xz $PKGS"

echo "Now we're going to install all the other development packages needed to build Yocto, please wait"

sudo zypper install $UPDATE_FLAG $PKGS

