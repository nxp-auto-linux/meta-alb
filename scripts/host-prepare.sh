#!/bin/sh

# This script can be used to prepare host packages for Yocto build.
# 
# sudo permission without password is required for the following command:
# (1) Redhat/Fedora/CentOS: yum
# (2) Ubuntu/Mint/Debian: apt-get
# (3) openSUSE/SUSE: zypper
#
# The following platforms are supported for poky rocko:
# Ubuntu: 14.10, 15.04, 15.10, 16.04 (LTS)
# Fedora: 22/23/24
# RHEL/CentOS: 7.x
# openSUSE: 13.2/Leap 42.1
# Debian: 8.x (Jessie), 9.x (Stretch)

SCRIPT_DIR=`readlink -f $(dirname $0)`

usage_message() {
    echo "Usage: $0 [-h] [-f]
    -h: display help
    -f: force install all needed host pkgs, running non-interactively
"
}

force_update=''
while getopts "fh" host_prepare_flag
do
    case $host_prepare_flag in
        f) force_update='true';
           ;;
        ?) usage_message;exit 1;
           ;;
    esac
done

# check host distribution
if [ -r /etc/lsb-release ] && grep Ubuntu /etc/lsb-release >/dev/null 2>&1
then
    # Ubuntu-based system
    . /etc/lsb-release
    distro="Ubuntu"
    release=${DISTRIB_RELEASE}
    hostpkg="apt-get"
elif [ -r /etc/debian_version ]
then
    # Debian-based
    distro="Debian"
    release=`cat /etc/debian_version`
    hostpkg="apt-get"
elif [ -r /etc/fedora-release ]
then
    # Fedora-based
    distro="Fedora"
    release=`sed -e 's/.*release \([0-9]*\).*/\1/' /etc/fedora-release`
    if which dnf >/dev/null;then
        hostpkg="dnf"
    else
        hostpkg="yum"
    fi
elif [ -r /etc/SuSE-release ]
then
    # SUSE-based.
    if grep openSUSE /etc/SuSE-release >/dev/null 2>&1
    then
        distro="openSUSE"
    else
        distro="SUSE"
    fi
    release=`cat /etc/SuSE-release | grep "VERSION" | sed -e 's/VERSION = //'`
    hostpkg="zypper"
elif [ -r /etc/redhat-release ]; then
    # Redhat-based
    if grep CentOS /etc/redhat-release >/dev/null 2>&1
    then
        distro="CentOS"
    else
        distro="Redhat"
    fi
    release=`cat /etc/redhat-release | sed -e 's/[A-Za-z ]* release //'`
    hostpkg="yum"
else
    echo "Error: Unsupported Distribution, Exit"
    exit 1
fi
export distro release force_update hostpkg

echo "Verifying sudo permission to execute $hostpkg command."
user=`whoami` || true
hostpkg_path=`which $hostpkg` || true
if [ -z "$hostpkg_path" ]; then
    echo "$hostpkg is required. Please install it and re-run this script."
    exit 1
fi
s=`yes "" | sudo -S -l 2>&1` || true
hostpkg_all=`echo $s | egrep "NOPASSWD:.*ALL"` || true
hostpkg_ok=`echo $s | egrep "NOPASSWD:.*$hostpkg_path"` || true
if [ -z "$hostpkg_ok" -a -z "$hostpkg_all" ]; then cat <<TXT
I ran the command: sudo -S -l which returned:

$s

This means you don't have sudo permission without password to execute 
$hostpkg command as root. This is needed to install host packages correctly.

To configure this, as root using the command "/usr/sbin/visudo",
and add the following line in the User privilege section:

$user ALL = NOPASSWD: $hostpkg_path

TXT
exit 1
fi

case "$distro" in
    'Ubuntu' | 'Mint' | 'Debian' )
        script="$SCRIPT_DIR/host-prepare-ubuntu-mint-debian.sh";
        ;;
    'Redhat' | 'CentOS' | 'Fedora')
        script="$SCRIPT_DIR/host-prepare-rhel-centos-fedora.sh";
        ;;
    'SUSE' | 'openSUSE')
        script="$SCRIPT_DIR/host-prepare-suse.sh";
        ;;
esac

/bin/sh -e $script
rc=$?
if [ "$rc" != "0" ];then
    echo "ERROR: Fail to install pkgs on host! See message above..."
    exit 1
fi

verlte () {
    [  "$1" = "`printf "$1\n$2" | sort -V | head -n1`" ]
}

verlt() {
    [ "$1" = "$2" ] && return 1 || verlte $1 $2
}

# Check for host kernel version and issue a warning if not 3.2.0 or later
# Yocto SDK requires this minimum version, although it is not clear
# if we should enforce it for running bitbake

EXPECTED_KERNEL="3.2.0"
verlt `uname -r` $EXPECTED_KERNEL
if [ $? = 0 ]; then
    echo "Warning: It is recommended to use a kernel > $EXPECTED_KERNEL"
fi

# Check for git version and issue an error if not 1.8.3.1 or greater

EXPECTED_GIT="1.8.3.1"
CURRENT_GIT=`git --version | rev | cut -d" " -f1 | rev`
verlt $CURRENT_GIT $EXPECTED_GIT
if [ $? = 0 ]; then
    echo "Found git version $CURRENT_GIT. You must use at least version $EXPECTED_GIT"
fi

# Check for tar version and issue an error if not 1.27 or greater

EXPECTED_TAR="1.27"
CURRENT_TAR=`tar --version | head -n 1 | rev | cut -d" " -f1 | rev`
verlt $CURRENT_TAR $EXPECTED_TAR
if [ $? = 0 ]; then
    echo "Found tar version $CURRENT_TAR. You must use at least version $EXPECTED_TAR"
fi

# Make sure we have python 3.4 or later installed
PYTHON_PATH="/opt/python-3.4.0"
PYTHON_v3=`/usr/bin/env python3 -c 'import sys
print (sys.version_info >= (3, 4, 0) and "1" or "0")'`
if [ "$PYTHON_v3" = '0' ]; then
    echo "Python version 3.x (3.4.0 or greater) is required. For example, install 3.4.0 at $PYTHON_PATH:
    (1) wget http://www.python.org/ftp/python/3.4.0/Python-3.4.0.tgz 
    (2) tar -zxvf Python-3.4.0.tgz 
    (3) cd Python-3.4.0
    (4) ./configure --prefix=$PYTHON_PATH && make
    (5) su -
    (6) make install
Then execute the following command and re-run this script:
    export PATH=$PYTHON_PATH/bin:\$PATH"
    exit 1
fi

echo -e "\nDone. You're ready to go with Yocto build now"

