#
# This is a generic recipe to generate an .itb image containing
# just the kernel and dtb to install as boot image into /boot
# Technically, this could be part of the standard kernel build
# mechanisms, but hacking them in a layer is more complicated
# than providing a generic recipe.
#
DEPENDS += "u-boot-mkimage-native virtual/kernel"

S = "${WORKDIR}"

# We do not want to create a deployed image through the class as
# that method is set up for images. For a standard recipe, we want
# to use a standard deploy task with the standard names!
ITB_ITS_FILE = "${IMAGE_BASENAME}-${MACHINE}.its"
ITB_DEPLOYDIR = ""
inherit deploy

inherit image_types_fsl_itb

do_install[depends] += "virtual/kernel:do_deploy"

ITBIMAGE_BASE_NAME = "kernel${ITB_ROOTFS_NAME_EXT}"
ITBIMAGE_FULL_NAME = "${ITBIMAGE_BASE_NAME}-${MACHINE}-${DATETIME}"
ITBIMAGE_FULL_NAME[vardepsexclude] = "DATETIME"

do_install() {
	# Generate the image
	IMAGE_CMD_itb
	
	# Install the image into /boot
	install -d ${D}/boot
	install -m 644 ${S}/kernel.itb ${D}/boot/${ITBIMAGE_FULL_NAME}.itb
	ln -sf ${ITBIMAGE_FULL_NAME}.itb ${D}/boot/${ITBIMAGE_BASE_NAME}.itb
}

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 644 ${S}/kernel.itb ${DEPLOYDIR}/${ITBIMAGE_FULL_NAME}.itb
	ln -sf ${ITBIMAGE_FULL_NAME}.itb ${DEPLOYDIR}/${ITBIMAGE_BASE_NAME}.itb
}

addtask deploy after do_install
PACKAGES += "${PN}-image"
FILES_${PN}-image += "/boot"
