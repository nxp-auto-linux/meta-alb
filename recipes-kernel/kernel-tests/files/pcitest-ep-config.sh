#!/bin/bash
#
# This script creates an EP function device (func1) and configures it,
# provided that the EPF support has been enabled in the kernel:
# - Set Vendor ID and Device ID to Freescale (NXP) and S32G2
# - Enable 8 MSIs and 16 MSI-Xs
# Then links the function device to the corresponding controller.
# Note: Only one controller should be configured as EP, as the script
# links the function device to the first controller it finds
# configured as EP.

set -x
WORKDIR="${PWD}"
if [ -d '/sys/kernel/config/pci_ep/' ]; then
        cd /sys/kernel/config/pci_ep/
        mkdir -p functions/pci_epf_test/func1

        echo 0x1957 > functions/pci_epf_test/func1/vendorid
        echo 0x4002 > functions/pci_epf_test/func1/deviceid
        echo 8 > functions/pci_epf_test/func1/msi_interrupts
        echo 16 > functions/pci_epf_test/func1/msix_interrupts

        for entry in `ls ./controllers/`; do
                echo "Linking EPF func1 to ${entry}"
                ln -s functions/pci_epf_test/func1 controllers/${entry}/
                break
        done
fi
cd ${WORKDIR}
set +x
