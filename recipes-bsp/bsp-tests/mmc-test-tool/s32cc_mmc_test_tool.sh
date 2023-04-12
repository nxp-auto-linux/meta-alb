#!/bin/bash

# Copyright 2023 NXP

# eMMC Script test utilitary based on
# Linux 'mmc_test.c' kernel module.
# To be used with flashimage (fsl-image-flash).

if [ $# -ne 0 ]; then
        echo "eMMC Linux script test utilitary"
        echo "Usage: $0"
		printf "\nExample:\n"
		echo "$(whoami)@$(hostname):/# $0"
		printf "\n"
        exit 1
fi

set -o pipefail

MMC_DEV="$(ls /sys/bus/mmc/devices)"

# Test if SD/eMMC is available
if [ -z "$MMC_DEV" ]; then
	echo "SD/eMMC is not available. Please check SD/eMMC probing."
	exit 1
fi

MMC_PART="$(echo "$MMC_DEV" | xargs -n1 | cut -f1 -d:)"
MMC_MODULE_NAME="mmc_test"
MMC_MODULE="mmc_test.ko"
MMC_MODULE_PATH="/lib/modules/$(uname -r)/kernel/drivers/mmc/core/$MMC_MODULE"

function validate_test_number() {
        local re='^[0-9]+$'

        if ! [[ "$1" =~ $re ]]; then
                echo "Error: Not a valid number"
                echo "Please select a number from above listed test cases:"
                return 1
        fi

        return 0
}

function query_and_run_test() {
        echo "Enter test case number: "
        read -p "" test_case_num

        while ! validate_test_number "$test_case_num"; do
                read -p "" test_case_num
        done

        echo "$test_case_num" > /sys/kernel/debug/"$MMC_PART"/"$MMC_DEV"/test
        printf "\n"
}

function run_other_test() {
        local text_to_display="Want to run another test? (y/n)"

        while true; do
                # workaround to force a console flush
                sleep 0

                printf "\n"
                echo "$text_to_display"
                read -p "" answer

                case "$answer" in
                "y")
                        query_and_run_test
                        text_to_display="Want to run another test? (y/n)"
                        ;;
                "n")
                        exit 1
                        ;;
                *)
                        text_to_display="Invalid answer. Please select (y/n)"
                        ;;
                esac
        done
}

if [ ! -f "$MMC_MODULE_PATH" ]; then
        echo "$MMC_MODULE kernel module does not exist"
        echo "Please make sure that Linux Kernel was compiled with CONFIG_MMC_TEST=m"
        exit 1
fi

if ! modprobe "$MMC_MODULE_NAME"; then
        echo "Failed to probe $MMC_MODULE_NAME kernel module, error: $?"
        exit 1
fi

# unbind the mmc block driver
echo "$MMC_DEV" > /sys/bus/mmc/drivers/mmcblk/unbind

# bind the 'mmc_test' driver
echo "$MMC_DEV" > /sys/bus/mmc/drivers/mmc_test/bind

mount -t debugfs debugfs /sys/kernel/debug/ 2>/dev/null

printf "\n"
echo "Please select one of the following test cases for eMMC"
cat /sys/kernel/debug/"$MMC_PART"/"$MMC_DEV"/testlist
printf "\n"

query_and_run_test
run_other_test
