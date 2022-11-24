#
#Copyright 2018 NXP
#

require recipes-fsl/images/fsl-image-auto.bb

inherit deploy
inherit image_types_fsl_itb

do_prepare_recipe_sysroot[noexec] = "1"
do_populate_lic[noexec] = "1"
do_rootfs[noexec] = "1"
do_image_qa[noexec] = "1"
do_image[noexec] = "1"
do_image_cpio[noexec] = "1"
do_image_tar[noexec] = "1"
do_image_complete[noexec] = "1"

do_deploy[nostamp] = "1"

ITB_ROOTFS_BASENAME = "fsl-image-auto"
ITB_ROOTFS_TYPE = "cpio"
ITB_ROOTFS_COMPRESSION = "gz"
KERNEL_ITS_FILE = ""
ITB_KERNEL_LOAD = "0x80080000"
ITB_KERNEL_ENTRY = "0x80080000"
ITB_DTB_LOAD = "0x82000000"

do_deploy[depends] += "u-boot-mkimage-native:do_populate_sysroot virtual/kernel:do_deploy ${ITB_ROOTFS_BASENAME}:do_build"

do_deploy() {
        IMAGE_CMD:itb
}

addtask deploy before do_build after do_unpack

COMPATIBLE_MACHINE = "s32r45sim|s32g274sim"
