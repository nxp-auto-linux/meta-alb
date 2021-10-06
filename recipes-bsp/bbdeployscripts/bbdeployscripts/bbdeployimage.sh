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
FLASH_IMAGE="fsl-image-flash"
DISK="/dev/sda"
SIZE_P1="+180G"
SIZE_P2="+40G"
SIZE_P3=""
SIZE_P1_SECT="377487360"
SIZE_P2_SECT="83886080"
SIZE_P3_SECT=""

if [ -x "/sbin/sfdisk" ]; then
        SFDISK=/sbin/sfdisk
else
        SFDISK=/usr/sbin/sfdisk
fi

# Select the image file depending on the SoC we run on
SOCTYPE=unknown
BLUEBOXNAME=unknown
ppccheck=$(grep e6500 /proc/cpuinfo)
if [ "$ppccheck" != "" ]; then
        SOCTYPE=t4
        BLUEBOXNAME=bluebox
else
        corecheck=$(grep 0xd08$ /proc/cpuinfo)
        if [ "$corecheck" != "" ]; then
                # Cortex-A72
                corecount=$(grep -c processor /proc/cpuinfo)
                if [ "$corecount" == "16" ]; then
                        SOCTYPE=lx2160a
                        BLUEBOXNAME=bluebox3
                else
                        SOCTYPE=ls2084a
                        if [ "$(devmem2 $((0x520000000)) b|grep :.0x|sed s/.*:.//)" != "0x41" ]; then
                                BLUEBOXNAME=bluebox
                        else
                                BLUEBOXNAME=bbmini
                        fi
                fi
        else
                corecheck=$(grep 0xd07$ /proc/cpuinfo)
                if [ "$corecheck" != "" ]; then
                        # Cortex-A57
                        SOCTYPE=ls2080a
                        BLUEBOXNAME=bluebox
                fi
        fi
fi

MACHINE=${SOCTYPE}${BLUEBOXNAME}
if [ "$MACHINE" == "lx2160abluebox3" ]; then
        DISK="/dev/nvme0n1"
        SIZE_P1="+512G"
        SIZE_P2="+256G"
        SIZE_P3="+8G"
        SIZE_P1_SECT="1073741824"
        SIZE_P2_SECT="536870912"
        SIZE_P3_SECT="16777216"
fi

if [ "$DISK" == "/dev/sda" ]; then
        PART_PREFIX=${DISK}
else
        PART_PREFIX=${DISK}p
fi

#Write the flash image on both qspi memories
if [ "$MACHINE" == "lx2160abluebox3" ]; then
        echo "Erase and write /dev/mtd0..."
        flashcp --erase-all ${FLASH_IMAGE}-${MACHINE}.flashimage /dev/mtd0
        echo "Erase and write /dev/mtd1..."
        flashcp --erase-all ${FLASH_IMAGE}-${MACHINE}.flashimage /dev/mtd1
        echo "Writing the QSPI memories done..."
        echo
fi

#Format the eMMC
if [ "$MACHINE" == "lx2160abluebox3" ]; then
echo "Umount eMMC partitions..."
umount /dev/mmcblk1p*
echo "Erasing eMMC partitions ..."
dd if=/dev/zero of=/dev/mmcblk1 bs=512 count=1
echo "Creating new eMMC partitions..."
if [ -x "/sbin/fdisk" ]; then
/sbin/fdisk /dev/mmcblk1 << EOF
o
n
p
1


t
83
w
EOF

else
$SFDISK /dev/mmcblk1 << EOF
,,L
write

EOF
fi
echo "Formatting eMMC partition ..."
umount /dev/mmcblk1p1
dd if=/dev/zero of=/dev/mmcblk1p1 bs=512 count=1
yes | mkfs.ext4 /dev/mmcblk1p1
fi

# We prefer the desktop enabled rootfs over the standard one
if [ -z "$1" ]; then
        if [ -f "${DESKTOP_IMAGE}-${MACHINE}.tar.gz" ]; then
                ROOTFS_IMAGE=${DESKTOP_IMAGE}-${MACHINE}.tar.gz
        else
                ROOTFS_IMAGE=${DEFAULT_IMAGE}-${MACHINE}.tar.gz
        fi
else
        ROOTFS_IMAGE="$1"
fi

if [ ! -e "$ROOTFS_IMAGE" ]; then
        echo "'$ROOTFS_IMAGE' cannot be found in the current directory"
        exit 1
fi

# First, we unmount any SSD/NVMe partition and then we trash the MBR
# to start clean when partitioning the disk
echo "Umount SSD/NVMe partitions..."
umount ${PART_PREFIX}*
echo "Erasing SSD/NVMe partitioning ..."
dd if=/dev/zero of=$DISK bs=512 count=1
# We want to recreate a setup with two Linux partitions and a swap
# partition
echo "Erasing SSD/NVMe partitions ..."
dd if=/dev/zero of=$DISK bs=512 count=1
echo "Creating new SSD/NVMe partitions ..."
if [ -x "/sbin/fdisk" ]; then
/sbin/fdisk $DISK << EOF
o
n
p
1

$SIZE_P1
n
p
2

$SIZE_P2
n
p
3

$SIZE_P3
t
3
82
w
EOF
else
$SFDISK $DISK << EOF
,$SIZE_P1_SECT,L
,$SIZE_P2_SECT,L
,$SIZE_P3_SECT,S
write

EOF
fi

# We need this for the partition table to be properly reread on a
# clean drive
sleep 1
umount ${PART_PREFIX}*

# To be on the safe side, we zero out the first block of each partition
# Before creating filesystems
dd if=/dev/zero of=${PART_PREFIX}1 bs=512 count=1
dd if=/dev/zero of=${PART_PREFIX}2 bs=512 count=1
dd if=/dev/zero of=${PART_PREFIX}3 bs=512 count=1

# Now we create the new and empty filesystems
echo "Formatting SSD/NVMe partitions ..."
yes | mkfs.ext4 ${PART_PREFIX}1
yes | mkfs.ext4 ${PART_PREFIX}2
mkswap    ${PART_PREFIX}3

# We assume we are root and that we have only a rudimentary system
# without any automount capability, so we brute force our way in
mkdir -p "$ROOTFS"
umount "$ROOTFS"

# Finally, we can unpack our new rootfs!
echo
echo "Unpacking the new rootfs..."
echo
mount ${PART_PREFIX}1 "$ROOTFS"
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
echo "Done! SSD/NVMe is fully prepared now with the BlueBox image!"
echo "****************************************************************"
