#!/bin/sh

# $1 is the partition name of the QSPI Flash Memory
# $2 is the file to be written in the QSPI Flash Memory

if [ $# -ne 2 ]; then
	echo "Usage: $0 <qspi_partition> <file_to_write>"
	printf "\nExamples:\n"
	echo "$(whoami)@$(hostname):/# $0 FIP fip.bin-qspi"
	echo "$(whoami)@$(hostname):/# $0 Kernel Image"
	printf "\n"
	exit 1
fi

qspi_partition_name="$1"
file_to_write="$2"

if [ -z "$qspi_partition_name" ]; then
	echo "Partition name invalid. Please consider using one of the following:"
	cat /proc/mtd
	exit 1
fi

if [ ! -f "$file_to_write" ]; then
	echo "Specified file to write is invalid"
	exit 1
fi

filesize=$(stat -c %s "$file_to_write" 2>/dev/null)
filesize_exitcode=$?
if [ "$filesize_exitcode" -ne 0 ]; then
	echo "Failed to get size on specified file"
	exit 1
fi

headline="$(head -n1 /proc/mtd | sed 's/://g')"
headline_exitcode=$?
if [ "$headline_exitcode" -ne 0 ]; then
	echo "Failed to retrieve headline for mtd partitions"
	exit 1
fi

dev_index=$(echo "$headline" | xargs -n1 | grep dev -nw | cut -f1 -d:)
dev_index_exitcode=$?
if [ "$dev_index_exitcode" -ne 0 ]; then
	echo "Failed to obtain dev index"
	exit 1
fi

size_index=$(echo "$headline" | xargs -n1 | grep size -nw | cut -f1 -d:)
size_index_exitcode=$?
if [ "$size_index_exitcode" -ne 0 ]; then
	echo "Failed to obtain size index"
	exit 1
fi

mtd_partition=$(grep -w "$qspi_partition_name" /proc/mtd | cut -f "$dev_index" -d ' ' | sed 's/://g')
mtd_partition_exitcode=$?
if [ "$mtd_partition_exitcode" -ne 0 ]; then
	echo "Failed to get MTD partition"
	exit 1
fi
mtd_partition="/dev/$mtd_partition"

mtd_partition_size=$(grep -w "$qspi_partition_name" /proc/mtd | cut -f "$size_index" -d ' ')
mtd_partition_size_exitcode=$?
if [ "$mtd_partition_size_exitcode" -ne 0 ]; then
	echo "Failed to get MTD partition size"
	exit 1
fi

mtd_partition_size=$((0x$mtd_partition_size))
mtd_partition_size_exitcode=$?
if [ "$mtd_partition_size_exitcode" -ne 0 ]; then
	echo "Failed to convert partition_size to decimal value"
	exit 1
fi

if [ "$filesize" -gt "$mtd_partition_size" ]; then
	echo "$file_to_write is greater in size than partition: $mtd_partition"
	exit 1
fi

erase_sector_size="$(mtdinfo "$mtd_partition" 2>/dev/null | grep -w Eraseblock | xargs | cut -f 3 -d ' ')"
erase_sector_size_exitcode=$?
if [ "$erase_sector_size_exitcode" -ne 0 ]; then
	echo "Failed to obtain erase_sector_size"
	exit 1
fi

sectors_to_erase=$(( mtd_partition_size / erase_sector_size ))
sectors_to_erase_exitcode=$?
if [ "$sectors_to_erase_exitcode" -ne 0 ]; then
	echo "Failed to compute sectors_to_erase"
	exit 1
fi

mtd_partition_offset="0x0"
if ! flash_erase "$mtd_partition" "$mtd_partition_offset" "$sectors_to_erase"; then
	echo "flash_erase command failed with error: $?"
	exit 1
fi

if ! mtd_debug write "$mtd_partition" "$mtd_partition_offset" "$filesize" "$file_to_write"; then
	echo "mtd_debug write command failed with error: $?"
	exit 1
fi

padding_size=$((sectors_to_erase * erase_sector_size - filesize))
padding_size_exitcode=$?
if [ "$padding_size_exitcode" -ne 0 ]; then
	echo "Failed to compute 'padding_size'"
	exit 1
fi

if [ $padding_size -gt 0 ]; then
	if ! mtd_debug write "$mtd_partition" "$filesize" "$padding_size" /dev/zero; then
		echo "mtd_debug write for padding command failed with error: $?"
		exit 1
	fi
fi
