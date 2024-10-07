
########################################################
# General GPT settings
########################################################

OPTIMAL_ALIGNMENT_KIB = "1024"

get_sector_alignment () {
	SECTOR_SIZE="$1"
	SECTOR_ALIGNMENT=$(echo "${OPTIMAL_ALIGNMENT_KIB} * 1024 / ${SECTOR_SIZE}" | bc)

	printf "%s" "${SECTOR_ALIGNMENT}"
}

PROTECTIVE_MBR_NUM_SECTORS = "1"
GPT_HEADER_NUM_SECTORS = "1"
# the standard size of the partition entry array is 16K,
# regardless of the SECTOR_SIZE
get_partition_entry_array_num_sectors () {
	SECTOR_SIZE="$1"
	PARTITION_ENTRY_ARRAY_NUM_SECTORS=$(echo "16 * 1024 / ${SECTOR_SIZE}" | bc)

	printf "%s" "${PARTITION_ENTRY_ARRAY_NUM_SECTORS}"
}

# compute the size of a GPT Header + Partition Entry Array
get_gpt_size_kib () {
	SECTOR_SIZE="$1"
	PARTITION_ENTRY_ARRAY_NUM_SECTORS=$(get_partition_entry_array_num_sectors "${SECTOR_SIZE}")
	GPT_SIZE_KIB="$(echo "(${GPT_HEADER_NUM_SECTORS} + ${PARTITION_ENTRY_ARRAY_NUM_SECTORS}) * ${SECTOR_SIZE} / 1024" | bc)"
	printf "%s" "${GPT_SIZE_KIB}"
}

# for optimal performance, it is recommended that the starting sector 
# for a partition is a multiple of SECTOR_ALIGNMENT, such that START_SECTOR % SECTOR_ALIGNMENT == 0
get_first_partition_start_sector () {
	SECTOR_SIZE="$1"
	FIRST_PARTITION_START_SECTOR=$(get_sector_alignment "${SECTOR_SIZE}")
	printf "%s" "${FIRST_PARTITION_START_SECTOR}"
}

get_first_partition_start_kib () {
	SECTOR_SIZE="$1"
	FIRST_PARTITION_START_SECTOR=$(get_first_partition_start_sector "${SECTOR_SIZE}")
	FIRST_PARTITION_START_KIB="$(convert_sectors_to_kib "${FIRST_PARTITION_START_SECTOR}" "${SECTOR_SIZE}")"
	printf "%s" "${FIRST_PARTITION_START_KIB}"
}

# create a partition with given name,
# size and fstype
create_partition () {
	LUN_NAME="$1"
	SECTOR_SIZE="$2"
	PARTITION_NAME="$3"
	PARTITION_SIZE_SECTORS="$4"
	PARTITION_FSTYPE="$5"

	current_table=$(parted -s "${LUN_NAME}" unit s print)

	last_partition_start_sector=$(echo "${current_table}" | tail -n 1  | xargs | sed -r 's/\s+/ /g' | cut -d ' ' -f 2 | sed 's/s//g')
	if [ "${last_partition_start_sector}" = "Start" ]; then
		FIRST_PARTITION_START_SECTOR=$(get_first_partition_start_sector ${SECTOR_SIZE})
		parted -s "${LUN_NAME}" unit s mkpart "${PARTITION_NAME}" "${PARTITION_FSTYPE}" "${FIRST_PARTITION_START_SECTOR}" "$(echo "${FIRST_PARTITION_START_SECTOR} + ${PARTITION_SIZE_SECTORS} - 1" | bc)"
	else
		last_partition_size_sectors=$(echo "${current_table}" | tail -n 1  | xargs | sed -r 's/\s+/ /g' | cut -d ' ' -f 4 | sed 's/s//g')
		new_partition_start_sector=$(compute_next_aligned_gpt_partition_start_sector "${last_partition_start_sector}" "${last_partition_size_sectors}" "${SECTOR_SIZE}")
		parted -s "${LUN_NAME}" unit s mkpart "${PARTITION_NAME}" "${PARTITION_FSTYPE}" "${new_partition_start_sector}" "$(echo "${new_partition_start_sector} + ${PARTITION_SIZE_SECTORS} - 1" | bc)"
	fi
}

# write an artifact to a partition, at a given offset
write_partition () {
	INPUT_ARTIFACT="$1"
	LUN_NAME="$2"
	SIZE_KiB="$3"
	PARTITION_OFFSET="$4"
	
	dd if="${INPUT_ARTIFACT}" of="${LUN_NAME}" bs=1KiB count="${SIZE_KiB}" seek="${PARTITION_OFFSET}" conv=fsync,notrunc
}

# print layout for debugging purposes
print_layout () {
	layout=$(parted -s "$1" unit KiB print)
	printf "%s" "${layout}"
}

# function to determine the next GPT partition
# start sector for optimal alignment,
# with respect to SECTOR_ALIGNMENT
compute_next_aligned_gpt_partition_start_sector () {
	CURRENT_PARTITION_START_SECTOR="$1"
	CURRENT_PARTITION_SIZE_SECTORS="$2"
	SECTOR_SIZE="$3"
	SECTOR_ALIGNMENT=$(get_sector_alignment ${SECTOR_SIZE})
	
	NEXT_PARTITION_START_SECTOR="$(echo "((${CURRENT_PARTITION_START_SECTOR} + ${CURRENT_PARTITION_SIZE_SECTORS} + (${SECTOR_ALIGNMENT} - 1)) / ${SECTOR_ALIGNMENT}) * ${SECTOR_ALIGNMENT}" | bc)"

	printf "%s" "${NEXT_PARTITION_START_SECTOR}"
}

# convert KiB to number of sectors
# with respect to SECTOR_SIZE
convert_kib_to_sectors () {
	NUM_KIB="$1"
	SECTOR_SIZE="$2"

	NUM_SECTORS="$(echo "(${NUM_KIB} * 1024) / ${SECTOR_SIZE}" | bc)"
	
	printf "%s" "${NUM_SECTORS}"
}

# compute a number of sectors to KiB
# with respect to SECTOR_SIZE
convert_sectors_to_kib () {
	NUM_SECTORS="$1"
	SECTOR_SIZE="$2"

	NUM_KIB="$(echo "${NUM_SECTORS} * ${SECTOR_SIZE} / 1024" | bc)"
	
	printf "%s" "${NUM_KIB}"
}

# compute the size of an artifact in KiB
compute_artifact_size_kib () {
	ARTIFACT_LOCATION="$1"

	ARTIFACT_DIMENSION_BYTES="$(stat -L -c "%s" "${ARTIFACT_LOCATION}")"
	ARTIFACT_DIMENSION_KIB="$(echo "(${ARTIFACT_DIMENSION_BYTES} + 1023) / 1024" | bc)"

	printf "%s" "${ARTIFACT_DIMENSION_KIB}"
}

# round up size to a certain unit
roundup_size_kib () {
	ARTIFACT_DIMENSION_KIB="$1"
	ROUND_UNIT_KIB="$2"

	ROUNDED_SIZE_KIB="$(echo "((${ARTIFACT_DIMENSION_KIB} + (${ROUND_UNIT_KIB} - 1)) / ${ROUND_UNIT_KIB}) * ${ROUND_UNIT_KIB}" | bc)"

	printf "%s" "${ROUNDED_SIZE_KIB}"
}
