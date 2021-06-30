#
# Copyright 2017-2021 NXP
#

require recipes-fsl/images/fsl-image-base.bb
require recipes-fsl/images/fsl-image-core-common.inc
include recipes-fsl/images/fsl-image-s32-common.inc

# copy the manifest and the license text for each package to image
COPY_LIC_MANIFEST = "1"
COPY_LIC_DIRS = "1"

IMAGE_INSTALL += " \
    kernel-devicetree \
    packagegroup-core-buildessential \
    packagegroup-core-nfs-server \
    packagegroup-core-tools-debug \
"

# Benchmark tools
IMAGE_INSTALL += "dhrystone fio"

IMAGE_INSTALL_append_gen1 = " perf"

# Userspace support for QSPI Flash under Linux for S32GEN1 platforms
IMAGE_INSTALL_append_gen1 = " mtd-utils "

# Supporting complex evaluation scenarios
IMAGE_INSTALL += "openssl-misc"
IMAGE_INSTALL_append_s32 += "openssl openssl-dev libcrypto libssl openssl-conf openssl-engines openssl-bin"
IMAGE_INSTALL_remove_s32 += "ipsec-tools"

# Increase the freespace
IMAGE_ROOTFS_EXTRA_SPACE ?= "54000"

# Enable LXC features.
# On LS2 enable it by default. On s32, only by DISTRO_FEATURE
LXC_INSTALL_PACKAGES = "lxc debootstrap"
IMAGE_INSTALL_append_s32 = "${@bb.utils.contains('DISTRO_FEATURES', 'lxc', ' ${LXC_INSTALL_PACKAGES}', '', d)}"
IMAGE_INSTALL_append_ls2 = " ${LXC_INSTALL_PACKAGES}"

# SFTP server
IMAGE_INSTALL_append = " openssh openssh-sftp openssh-sftp-server "

# Other useful tools
IMAGE_INSTALL_append = " rsync irqbalance i2c-tools"

# PCIe demos and test apps
PCIE_INSTALL_PACKAGES ?= " demo-pcie-shared-mem demo-virt-eth"
IMAGE_INSTALL_append = "${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', ' ${PCIE_INSTALL_PACKAGES}', '', d)}"

