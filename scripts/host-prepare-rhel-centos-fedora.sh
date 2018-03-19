#!/bin/sh

UPDATE_FLAG=''
if test $force_update; then UPDATE_FLAG='-y';fi

echo "Install packages needed to build Yocto, please wait, it may take a while"

# pkgs listed in yocto doc
# http://www.yoctoproject.org/docs/current/yocto-project-qs/yocto-project-qs.html#packages
PKGS="gawk make wget tar bzip2 gzip python unzip perl patch \
     diffutils diffstat git cpp gcc gcc-c++ glibc-devel texinfo \
     chrpath socat SDL-devel xterm"
if [ "Fedora" = "$distro" ]; then
    PKGS="$PKGS ccache perl-Data-Dumper perl-Text-ParseWords perl-Thread-Queue findutils which"
fi
# pkgs required for fsl use
PKGS="vim-common redhat-lsb xz perl-String-CRC32 dos2unix screen $PKGS"

if [ "yum" = "$hostpkg" ];then
    sudo yum $UPDATE_FLAG groupinstall "Development Tools"
    sudo yum $UPDATE_FLAG install $PKGS
elif [ "dnf" = "$hostpkg" ];then
    sudo dnf $UPDATE_FLAG group install "Development Tools"
    sudo dnf $UPDATE_FLAG install $PKGS
fi

# processing chrpath
[ -r /etc/redhat-release ] && series=`sed -e 's,.*\([0-9]\)\..*,\1,g' /etc/redhat-release`
# for RHEL, chrpath is ONLY availabe on series 6.x
if [ "Redhat" = "$distro" -a "6" != "$series" ]; then
    # check if chrpath is installed.
    if [ -z "$(rpm -qa chrpath)" ]; then
        echo "chrpath is required. Install it as follows:
        (1) download the package from http://mirror.centos.org/centos/${series}/extras/
        (2) sudo rpm -Uhv chrpath-0.13-3.el${series}.centos.i386.rpm
            or
            sudo rpm -Uhv chrpath-0.13-3.el${series}.centos.x86_64.rpm
        Then re-run this script."
        exit 1
    fi
fi

