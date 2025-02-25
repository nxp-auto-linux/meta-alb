# Copyright 2021,2025 NXP

require ${@bb.utils.contains('DISTRO_FEATURES', 'llce-fw-load', 'linux-firmware-llce.inc', '', d)}

NXP_FIRMWARE_LOCAL_DIR ?= "."
