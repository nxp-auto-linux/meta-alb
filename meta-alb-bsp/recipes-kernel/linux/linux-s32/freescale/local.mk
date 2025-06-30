# SPDX-License-Identifier: GPL-2.0+ OR BSD-3-Clause
#
# Copyright 2025 NXP
#
# Makefile for out-of-tree device tree

DTC_EXTRA = -I$(src)
dtb-$(CONFIG_ARCH_S32) += ${KERNEL_DEVICETREE}
