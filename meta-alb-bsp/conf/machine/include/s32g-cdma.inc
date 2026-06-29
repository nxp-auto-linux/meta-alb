# Copyright 2026 NXP

DEFAULT_DT := "${KERNEL_DEVICETREE}"
CDMA_DT := "${@d.getVar('KERNEL_DEVICETREE', True).replace('.${FLASHIMAGE_DTB_SUFFIX}', '-cdma-pipe10.${FLASHIMAGE_DTB_SUFFIX}')}"
KERNEL_DEVICETREE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pcie-services-demos', '${CDMA_DT}', '${DEFAULT_DT}', d)}"
FLASHIMAGE_DTB_FILE = "${@d.getVar('KERNEL_DEVICETREE').replace('freescale/', '')}"
