#!/bin/bash
#
# This script can be run from a FLASH booted Linux root prompt to
# replace the current SSD based rootfs with a new image, while
# retaining the current /home directory and key config files.
#
# <Heinz.Wrobel@nxp.com>
#
# SD-Card or USB stick preparation
#       First partition must be FAT and must contain this script and
#       the rootfs tar.gz image file.
#
#
# Script usage:
#       [Assumption: The NOR flash contains a BlueBox compatible Linux]
#       [Reset the board and abort the boot process by pressing a key]
#       run boot_from_flash
#       [Wait until the flash based Linux has booted and login as root]
#       [Insert the SD card and wait for it to be mounted]
#               cd /run/media/mmcblk0p1
#               ./bbreplacerootfs.sh
#       [1. Alternative: Use a USB stick /dev/run/media/usb...]
#       [2. Alternative: Different partition, e.g., /run/media/sda2]
# 
#
ROOTFS="/run/media/sda1"

DESKTOP_IMAGE="fsl-image-blueboxdt"
DEFAULT_IMAGE="fsl-image-auto"

# Select the image file depending on the SoC we run on
IMAGETYPE=unknown
SOCTYPE=unknown
BLUEBOXNAME=unknown
ppccheck=`cat /proc/cpuinfo |grep e6500`
if [ "$ppccheck" != "" ]; then
        SOCTYPE=t4
        BLUEBOXNAME=bluebox
else
        corecheck=`cat /proc/cpuinfo |grep 0xd08$`
        if [ "$corecheck" != "" ]; then
                # Cortex-A72
                SOCTYPE=ls2084a
                if [ "`devmem2 $((0x520000000)) b|grep :.0x|sed s/.*:.//`" != "0x41" ]; then
                        BLUEBOXNAME=bluebox
                else
                        BLUEBOXNAME=bbmini
                fi
        fi
        corecheck=`cat /proc/cpuinfo |grep 0xd07$`
        if [ "$corecheck" != "" ]; then
                # Cortex-A57
                SOCTYPE=ls2080a
                BLUEBOXNAME=bluebox
        fi
fi

# We prefer the desktop enabled rootfs over the standard one
if [ -z "$1" ]; then
        if [ -f "${DESKTOP_IMAGE}-${SOCTYPE}${BLUEBOXNAME}.tar.gz" ]; then
                ROOTFS_IMAGE=${DESKTOP_IMAGE}-${SOCTYPE}${BLUEBOXNAME}.tar.gz
        else
                ROOTFS_IMAGE=${DEFAULT_IMAGE}-${SOCTYPE}${BLUEBOXNAME}.tar.gz
        fi
else
        ROOTFS_IMAGE="$1"
fi

if [ ! -e "$ROOTFS_IMAGE" ]; then
        echo "'$ROOTFS_IMAGE' cannot be found in the current directory"
        exit 1
fi

if [ ! -d "$ROOTFS" ]; then
        echo "The rootfs to replace is not mounted at '$ROOTFS'"
        exit 1
fi

# We have the new rootfs file and the destination now.
echo 
echo "Using $ROOTFS_IMAGE to update $ROOTFS..."
echo

# rescue relevant user files
# We just rescue some, and some are rescued and restored automagically
FILES2RESCUEANDRESTORE="/etc/hosts /etc/hostname /etc/sudoers /etc/default/locale /home/root/.bashrc /home/bluebox/.bashrc /etc/yum/repos.d"
FILES2RESCUE="${FILES2RESCUEANDRESTORE} /etc/network/interfaces"
currentdate=`date +%Y%m%d%H%M%S`
RESCUEPATH="/rescuedfiles"
RESCUEPATHDATE="${RESCUEPATH}/${currentdate}"
echo "Rescuing relevant user files from previous rootfs..."
echo
if [ -d "${ROOTFS}${RESCUEPATHDATE}" ]; then
        rm -r "${ROOTFS}${RESCUEPATHDATE}"
fi
for i in ${FILES2RESCUE}; do
        if [ -e "${ROOTFS}$i" ]; then
                if [ ! -d "${ROOTFS}${RESCUEPATHDATE}" ]; then
                        cd "$ROOTFS"
                        mkdir -m=0700 -p ".${RESCUEPATH}/"
                        mkdir -m=0700 -p ".${RESCUEPATHDATE}/"
                        cd - >/dev/null
                fi
                echo "Rescuing original $i to target '${RESCUEPATHDATE}'"
                cd "${ROOTFS}"
                if [ -d ".$i" ]; then
                        cp -R --parents --copy-contents ".$i" "${ROOTFS}${RESCUEPATHDATE}/"
                else
                        cp --parents --copy-contents ".$i" "${ROOTFS}${RESCUEPATHDATE}/"
                fi
                cd - >/dev/null
        fi
done
echo

# erase the old rootfs, preserving home and other things, and then unpack the new one
cd "$ROOTFS"
echo "Cleaning out the old root filesystem at ${ROOTFS}..."
for i in bin boot dev etc lib lib64 linuxrc media mnt opt proc root run sbin srv sys tmp usr var www; do
        echo "Erasing /$i..."
        rm -rf "./$i"
done
cd - >/dev/null

echo
echo "Unpacking the new rootfs..."
echo
(export EXTRACT_UNSAFE_SYMLINKS=1; tar -xz -C "$ROOTFS" -f "${ROOTFS_IMAGE}")
sync

echo "Restoring relevant user files from previous rootfs..."
echo
for i in ${FILES2RESCUEANDRESTORE}; do
        if [ -e "${ROOTFS}${RESCUEPATHDATE}$i" ]; then
                echo "Restoring original $i to new rootfs"
                cd "${ROOTFS}${RESCUEPATHDATE}"
                if [ -d ".$i" ]; then
                        cp -R --parents ".$i" "${ROOTFS}/" 
                else
                        cp --parents ".$i" "${ROOTFS}/" 
                fi
                cd - >/dev/null
        fi
done
echo

echo "Creating reference copy of rootfs image in /..."
echo
cp "${ROOTFS_IMAGE}" "$ROOTFS"
sync

echo "Done. You can reboot safely now ..."
exit 0
