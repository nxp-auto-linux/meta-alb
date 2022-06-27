#!/bin/bash

# $1 is the offset in the QSPI Flash Memory
# $2 is the file to be written in the QSPI Flash Memory

if [ $# -ne 2 ]; then
	echo "Usage: $0 <qspi_offset> <file_to_write>"
	printf "\nExamples:\n"
	echo "$(whoami)@$(hostname):/# $0 $FLASHIMAGE_FIP_OFFSET fip.s32-qspi"
	echo "$(whoami)@$(hostname):/# $0 $FLASHIMAGE_KERNEL_OFFSET Image"
	printf "\n"
	exit 1
fi

qspi_offset="$1"
file_to_write="$2"

FILESIZE=$(stat -c %s "$file_to_write" 2>/dev/null)
if [ $? -ne 0 ]; then
	echo "Failed to get size on specified file"
	exit 1
fi

FLASH_INTERFACE=/dev/mtd0

set -o pipefail
ERASE_SECTOR_SIZE="$(mtdinfo "$FLASH_INTERFACE" 2>/dev/null | grep Eraseblock | awk '{print $3}')"
if [ $? -ne 0 ]; then
	echo "Fail to compute ERASE_SECTOR_SIZE"
	exit 1
fi

SECTORS_TO_ERASE=$(( (FILESIZE / ERASE_SECTOR_SIZE) + 1 ))
if [ $? -ne 0 ]; then
	echo "Failed to compute SECTORS_TO_ERASE"
	exit 1
fi

if ! flash_erase "$FLASH_INTERFACE" "$qspi_offset" "$SECTORS_TO_ERASE"; then
	echo "flash_erase command failed with error: "$?""
	exit 1
fi

if ! mtd_debug write "$FLASH_INTERFACE" "$qspi_offset" "$FILESIZE" "$file_to_write"; then
	echo "mtd_debug write command failed with error: "$?""
	exit 1
fi

padding_size=$((SECTORS_TO_ERASE * ERASE_SECTOR_SIZE - FILESIZE))
if [ $? -ne 0 ]; then
	echo "Failed to compute 'padding_size'"
	exit 1
fi
if [ $padding_size -gt 0 ]; then
	padding_offset=$((qspi_offset + FILESIZE))
	if [ $? -ne 0 ]; then
		echo "Failed to compute 'padding_offset'"
		exit 1
	fi
	if ! mtd_debug write "$FLASH_INTERFACE" "$padding_offset" "$padding_size" /dev/zero; then
		echo "mtd_debug write for padding command failed with error: "$?""
		exit 1
	fi
fi
