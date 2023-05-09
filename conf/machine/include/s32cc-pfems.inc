# Copyright 2023 NXP

PFE_DT := "${KERNEL_DEVICETREE}"
PFEMS_DT := "${@d.getVar('KERNEL_DEVICETREE', True).replace('.dtb', '-pfems.dtb')}"
KERNEL_DEVICETREE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pfe-slave', '${PFEMS_DT}', '${PFE_DT}', d)}"
