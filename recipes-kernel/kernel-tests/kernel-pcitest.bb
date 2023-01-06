# Copyright 2020-2021 NXP

SUMMARY = "Kernel PCI test for Linux"
DESCRIPTION = "Kernel PCI test for Linux"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit linux-kernel-base kernel-arch
inherit kernelsrc

S = "${WORKDIR}/${BP}"

KERNEL_PCITEST_SRC ?= " \
             include \
             tools/arch \
             tools/build \
             tools/include \
             tools/lib \
             tools/Makefile \
             tools/pci \
             tools/scripts \
"

SRC_URI:append:s32cc = " file://pcitest-ep-config.sh"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

do_configure[prefuncs] += "copy_pci_source_from_kernel"
python copy_pci_source_from_kernel() {
    sources = (d.getVar("KERNEL_PCITEST_SRC") or "").split()
    src_dir = d.getVar("STAGING_KERNEL_DIR")
    dest_dir = d.getVar("S")
    bb.utils.mkdirhier(dest_dir)
    for s in sources:
        src = oe.path.join(src_dir, s)
        dest = oe.path.join(dest_dir, s)
        if not os.path.exists(src):
            bb.fatal("Path does not exist: %s. Maybe KERNEL_PCITEST_SRC does not match the kernel version." % src)
        if os.path.isdir(src):
            oe.path.copyhardlinktree(src, dest)
        else:
            bb.utils.copyfile(src, dest)
}

EXTRA_OEMAKE = '\
    CROSS_COMPILE=${TARGET_PREFIX} \
    ARCH=${ARCH} \
    CC="${CC}" \
    AR="${AR}" \
    LD="${LD}" \
    DESTDIR="${D}" \
'

do_compile() {
    unset CFLAGS
    oe_runmake -C tools/pci
}

do_install() {
    unset CFLAGS
    oe_runmake -C tools/pci install
}

do_install:append:s32cc() {
	# for some reason the file in SRC_URI is not copied to $WORKDIR
	install -m 0755 ${THISDIR}/files/pcitest-ep-config.sh ${D}${bindir}/pcitest-ep-config.sh
}

FILES:${PN} += "${bindir}"
RDEPENDS:${PN} += "bash"

PACKAGE_ARCH = "${MACHINE_ARCH}"
