# Copyright 2021,2025 NXP

require ${@bb.utils.contains('DISTRO_FEATURES', 'carvnet-fw-load', 'linux-firmware-carvnet.inc', '', d)}

NXP_FIRMWARE_LOCAL_DIR ?= "."
