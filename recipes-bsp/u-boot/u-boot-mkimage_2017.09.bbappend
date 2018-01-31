# We need not only the mkimage too, but also the mkenvimage tool
do_install_append () {
	install -d ${D}${bindir}
	install -m 0755 tools/mkenvimage ${D}${bindir}/uboot-mkenvimage
	ln -sf uboot-mkenvimage ${D}${bindir}/mkenvimage
}

