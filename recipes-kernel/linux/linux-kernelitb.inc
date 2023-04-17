#
# This is a generic recipe to generate an .itb image containing
# just the kernel and dtb to install as boot image into /boot
# Technically, this could be part of the standard kernel build
# mechanisms, but hacking them in a layer is more complicated
# than providing a generic recipe.
#

S = "${WORKDIR}"

# Using the smaller kernel targeted at flash deployment
ITB_KERNEL ?= '${@ "virtual/kernel" if ((d.getVar("FLASHIMAGE_KERNEL") or "") == "") else d.getVar("FLASHIMAGE_KERNEL") }'
ITB_KERNEL_BASENAME ?= '${@ "kernel" if ((d.getVar("FLASHIMAGE_KERNEL_BASENAME") or "") == "") else d.getVar("FLASHIMAGE_KERNEL_BASENAME") }'

# We do not want to create a deployed image through the class as
# that method is set up for images. For a standard recipe, we want
# to use a standard deploy task with the standard names!
ITB_ITS_FILE = "${IMAGE_BASENAME}-${MACHINE}.its"
ITB_DEPLOYDIR = ""
inherit deploy

inherit image_types_fsl_itb

do_install[depends] += "u-boot-mkimage-native:do_populate_sysroot ${ITB_KERNEL}:do_deploy"

ITBIMAGE_BASE_NAME = "${ITB_KERNEL_BASENAME}${ITB_ROOTFS_NAME_EXT}"
ITBIMAGE_FULL_NAME = "${ITBIMAGE_BASE_NAME}-${MACHINE}-${DATETIME}"
ITBIMAGE_FULL_NAME[vardepsexclude] = "DATETIME"

do_install() {
	# Generate the image
	configure_and_generate_itb
	
	# Install the image into /boot
	install -d ${D}/boot
	install -m 644 ${B}/${ITB_KERNEL_BASENAME}.itb ${D}/boot/${ITBIMAGE_FULL_NAME}.itb
	ln -sf ${ITBIMAGE_FULL_NAME}.itb ${D}/boot/${ITBIMAGE_BASE_NAME}.itb
}

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 644 ${B}/${ITB_KERNEL_BASENAME}.itb ${DEPLOYDIR}/${ITBIMAGE_FULL_NAME}.itb
	ln -sf ${ITBIMAGE_FULL_NAME}.itb ${DEPLOYDIR}/${ITBIMAGE_BASE_NAME}.itb
}

addtask deploy after do_install
PACKAGES += "${PN}-image"
FILES:${PN}-image += "/boot"
