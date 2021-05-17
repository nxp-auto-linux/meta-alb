#!/bin/bash
#
# This script can be run from a FLASH booted Linux root prompt to
# properly partition and deploy the image to the SSD to restore
# 'factory defaults'
#
# <Heinz.Wrobel@nxp.com>
#
# SD-Card or USB stick preparation
#       First partition must be FAT and must contain this script and
#       the rootfs tar.gz image file.
#       
#
# Script usage:
#       [Ensure that bbdeployimage.[itb|img] has been used to update the NOR]
#       [Reset the board and abort the boot process by pressing a key]
#       run boot_from_flash
#       [Wait until the flash based Linux has booted and login as root]
#       [Insert the SD card and wait for it to be mounted]
#               cd /run/media/mmcblk0p1
#               ./bbdeployimage.sh
#       [1. Alternative: Use a USB stick /dev/run/media/usb...]
#       
#
ROOTFS="/mnt/image"
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

# First, we unmount any SSD partition and then we trash the MBR
# to start clean when partitioning the disk
echo "Erasing SSD partitioning ..."
umount /dev/sda*
dd if=/dev/zero of=/dev/sda bs=512 count=1

# We want to recreate a setup with two Linux partitions and a swap
# partition
echo "Creating new SSD partitions ..."
if [ -e "/sbin/fdisk" ]; then
/sbin/fdisk /dev/sda << EOF
n
p
1

+180G
n
p
2

+40G
n
p
3


t
3
82
w
EOF
else
/sbin/sfdisk -uM /dev/sda << EOF
,182400,L
,42400,L
,,S
EOF
fi

# We need this for the partition table to be properly reread on a
# clean drive
sleep 1
umount /dev/sda*

# To be on the safe side, we zero out the first block of each partition
# Before creating filesystems
dd if=/dev/zero of=/dev/sda1 bs=512 count=1
dd if=/dev/zero of=/dev/sda2 bs=512 count=1
dd if=/dev/zero of=/dev/sda3 bs=512 count=1

# Now we create the new and empty filesystems
echo "Formatting SSD partitions ..."
mkfs.ext4 /dev/sda1
mkfs.ext4 /dev/sda2
mkswap    /dev/sda3

# We assume we are root and that we have only a rudimentary system
# without any automount capability, so we brute force our way in
mkdir -p "$ROOTFS"
umount "$ROOTFS"

# Finally, we can unpack our new rootfs!
echo
echo "Unpacking the new rootfs..."
echo
mount /dev/sda1 "$ROOTFS"
(export EXTRACT_UNSAFE_SYMLINKS=1; tar -xz -C "$ROOTFS" -f "${ROOTFS_IMAGE}")
sync

echo
echo "Creating reference copy of rootfs image in /..."
echo
cp "${ROOTFS_IMAGE}" "$ROOTFS"
sync
umount "$ROOTFS"

echo ""
echo ""
echo ""
echo "****************************************************************"
echo "Done! SSD is fully prepared now with the Blue Box image!"
echo "****************************************************************"


