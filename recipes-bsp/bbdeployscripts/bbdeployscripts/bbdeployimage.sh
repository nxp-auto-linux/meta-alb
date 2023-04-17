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
ROOTFSLOC="/run/media/"
DESKTOP_IMAGE="fsl-image-blueboxdt"
DEFAULT_IMAGE="fsl-image-auto"

# Defaults for SATA as most used interface
ROOTDEVNAME="SATA SSD"
ROOTDEV="sda"
ROOTDEVPARTPREFIX=""

# Select the image file depending on the SoC we run on
IMAGETYPE=unknown
SOCTYPE=unknown
BLUEBOXNAME=unknown
ppccheck=`grep e6500 /proc/cpuinfo`
if [ "$ppccheck" != "" ]; then
        SOCTYPE=t4
        BLUEBOXNAME=bluebox
else
        corecheck=$(grep 0xd08$ /proc/cpuinfo)
        if [ -n "$corecheck" ]; then
                # Cortex-A72
                svr=$(devmem2 0x01e000a4 w|grep :.0x|sed s/.*:.//)
                case ${svr:0:6} in
                        0x8736)
                                SOCTYPE=lx2160a
                                rcswsr29=$(devmem2 0x01e00170 w|grep :.0x|sed s/.*:.//)
                                if [ "${rcswsr29:4:2}" = "5F" ]; then
                                        BLUEBOXNAME=bluebox3
                                        ROOTDEVNAME="NVMe SSD"
                                        ROOTDEV="nvme0n1"
                                        ROOTDEVPARTPREFIX="p"
                                else
                                        BLUEBOXNAME=rdb2bluebox
                                fi
                                ;;
                        0x8709)
                                SOCTYPE=ls2084a
                                if [ "$(devmem2 $((0x520000000)) b|grep :.0x|sed s/.*:.//)" != "0x41" ]; then
                                        BLUEBOXNAME=bluebox
                                else
                                        BLUEBOXNAME=bbmini
                                fi
                                ;;
                esac
        else
                corecheck=$(grep 0xd07$ /proc/cpuinfo)
                if [ -n "$corecheck" ]; then
                        # Cortex-A57
                        SOCTYPE=ls2080a
                        BLUEBOXNAME=bluebox
                else
                        corecheck=$(grep 0xd03$ /proc/cpuinfo)
                        if [ -n "$corecheck" ]; then
                                # Cortex-A53
                                :
                        fi
                fi
        fi
fi

MACHINE="${SOCTYPE}${BLUEBOXNAME}"

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

echo ""
echo "Board: ${MACHINE}, massstorage is ${ROOTDEVNAME}"
echo ""


if [ ! -e "$ROOTFS_IMAGE" ]; then
        echo "'$ROOTFS_IMAGE' cannot be found in the current directory!"
        read -p "Do you want to continue with formatting the ${ROOTDEVNAME} anyway? [y/n]" -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
                exit 1
        fi
fi

# This is where we finally want to unpack
ROOTFSPREFIX="${ROOTFSLOC}${ROOTDEV}${ROOTDEVPARTPREFIX}"
ROOTFS="${ROOTFSPREFIX}1"

# First, we unmount any SSD partition and then we trash the MBR
# to start clean when partitioning the disk
echo "Erasing SSD partitioning on ${ROOTDEVNAME}..."
umount /dev/${ROOTDEV}*
dd if=/dev/zero of=/dev/${ROOTDEV} bs=512 count=64
sfdisk --delete /dev/${ROOTDEV}

# Determine the SSD size and come up with a reasonable partition scheme
ssdsize=$(blockdev --getsz /dev/${ROOTDEV})
ssdsizeM=$(printf "$ssdsize / 2048" | bc)
if [ $ssdsizeM -lt 65536 ]; then
        echo "Can't partition a disk of less than 64G"
        exit 1
fi
SIZE_P1="$(printf "($ssdsizeM * 0.7)/1" | bc)M"
SIZE_P2="$(printf "($ssdsizeM * 0.15)/1" | bc)M"
SIZE_P3="8G"

# We want to recreate a setup with two Linux partitions and a swap
# partition
echo "Creating new partitions on ${ROOTDEVNAME}..."
sfdisk -W always /dev/${ROOTDEV} << EOF
128M,${SIZE_P1},L
,${SIZE_P2},L
,${SIZE_P3},S
EOF

# We need this for the partition table to be properly reread on a
# clean drive
sleep 1
partprobe /dev/${ROOTDEV}
umount /dev/${ROOTDEV}*

# To be on the safe side, we zero out the first block of each partition
# Before creating filesystems. sfdisk should have cleaned the signature
# but we are very careful.
for part in 1 2 3; do
        dd if=/dev/zero of=/dev/${ROOTDEV}${ROOTDEVPARTPREFIX}$part bs=512 count=1
done

# Now we create the new and empty filesystems
echo "Formatting ${ROOTDEVNAME} partitions ..."
for part in 1 2; do
        mkfs.ext4 /dev/${ROOTDEV}${ROOTDEVPARTPREFIX}$part
done
mkswap    /dev/${ROOTDEV}${ROOTDEVPARTPREFIX}3

# We assume we are root and that we have only a rudimentary system
# without any automount capability, so we brute force our way in
# and mount the two created ext4 partitions.
for part in 1 2; do
        mkdir -p "${ROOTFSPREFIX}$part"
        umount "${ROOTFSPREFIX}$part"
done
echo
for part in 1 2; do
        mount "/dev/${ROOTDEV}${ROOTDEVPARTPREFIX}$part" "${ROOTFSPREFIX}$part"
        echo "Mounted /dev/${ROOTDEV}${ROOTDEVPARTPREFIX}$part on ${ROOTFSPREFIX}$part"
done

if [ ! -e "$ROOTFS_IMAGE" ]; then
        echo ""
        echo ""
        echo ""
        echo "****************************************************************"
        echo "Done! SSD is partitioned and partitions are mounted!"
        echo "****************************************************************"

        # Done without rootfs
        exit 0
fi

# Finally, we can unpack our new rootfs!
echo
echo "Unpacking the new rootfs..."
echo
(export EXTRACT_UNSAFE_SYMLINKS=1; tar -xz -C "${ROOTFS}" -f "${ROOTFS_IMAGE}")
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


