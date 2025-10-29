# Copyright 2025 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-s32:"

# Determine the device tree source name.
KERNEL_DTS_FILE = "${@ (d.getVar('KERNEL_DEVICETREE_EXT') or '').replace('.dtb', '.dts') }"

SRC_URI:append = "\
    file://freescale/local.mk \
    file://${DT_SOURCE_SUBDIR}/${KERNEL_DTS_FILE} \
    "

DT_SOURCE_SUBDIR ?= "dts"
KERNEL_EXTRA_ARGS += " EXTERNAL_DTBS_MK=${WORKDIR}/freescale/local.mk"

# Use realpath to replace the symbol links.
KERNEL_DEVICETREE_PATH = "${@os.path.realpath(d.getVar('S') + \
	'/arch/arm64/boot/dts')}"
EXT_DTB_PATH = "${STAGING_KERNEL_BUILDDIR}/${KERNEL_DEVICETREE_EXT}"

# Use the device tree from the ${STAGING_KERNEL_BUILDDIR}. The path to it should
# be relative to ${KERNEL_DEVICETREE_PATH}.
KERNEL_DEVICETREE = "freescale/../${@os.path.relpath(d.getVar('EXT_DTB_PATH'), \
	d.getVar('KERNEL_DEVICETREE_PATH'))}"

# Copy the device tree source into STAGING_KERNEL_BUILDDIR to make sure the resulting
# dtb is removed during do_clean task.
do_compile:prepend() {
    dtb_dirname="$(dirname ${KERNEL_DEVICETREE_EXT})"
    src_dir="${WORKDIR}/${DT_SOURCE_SUBDIR}/${dtb_dirname}/"

    set -- "${src_dir}/"*
    if [ -f "$1" ]; then
        cp -vf "$@" "${STAGING_KERNEL_BUILDDIR}/"
    else
        bbwarn "Nothing found in ${src_dir}"
    fi
}
