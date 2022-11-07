#!/bin/sh

UPDATE_FLAG=''
if test $force_update; then UPDATE_FLAG='-y';fi

echo "Install packages needed to build Yocto, please wait, it may take a while"

# pkgs listed in yocto doc
# https://www.yoctoproject.org/docs/2.4.2/ref-manual/ref-manual.html#intro-requirements
PKGS="curl gawk make wget tar bzip2 gzip python python3 unzip perl patch \
     diffutils diffstat git cpp gcc gcc-c++ glibc-devel texinfo \
     chrpath socat perl-Data-Dumper perl-Text-ParseWords perl-Thread-Queue \
     python3-pip xz which SDL-devel xterm ncurses-devel lz4 zstd"
if [ "Fedora" = "$distro" ]; then
    PKGS="$PKGS ccache perl-bignum python3-pexpect findutils file cpio"
fi
# pkgs required for fsl use
PKGS="$PKGS vim-common redhat-lsb xz perl-String-CRC32 dos2unix screen parted \
     dosfstools mtools udev"

# Extra Packages for Enterprise Linux (i.e. epel-release) is a collection
# of packages from Fedora built on RHEL/CentOS for easy installation of
# packages not included in enterprise Linux by default.
# You need to install these packages separately.
# The makecache command consumes additional Metadata from epel-release.

if [ "yum" = "$hostpkg" ];then
    sudo yum $UPDATE_FLAG install epel-release
    sudo yum makecache
    sudo yum $UPDATE_FLAG groupinstall "Development Tools"
    sudo yum $UPDATE_FLAG install $PKGS
elif [ "dnf" = "$hostpkg" ];then
    sudo dnf $UPDATE_FLAG install epel-release
    sudo dnf makecache
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

