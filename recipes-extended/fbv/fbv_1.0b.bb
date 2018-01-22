DESCRIPTION = "Frame Buffer Viewer"
LICENSE = "GPLv2"
DEPENDS = "libpng jpeg giflib"
PR = "r5"
LIC_FILES_CHKSUM = "file://COPYING;md5=130f9d9dddfebd2c6ff59165f066e41c"
SRC_URI = "http://s-tech.elsat.net.pl/fbv/fbv-1.0b.tar.gz \
	file://0002-cross.patch \
	file://0003-fix-24bpp-support-on-big-endian.patch \
	file://0004-fix-bgr555.patch \
	file://0005-giflib.patch \
	file://0006-include.patch \
	file://0007-libpng15.patch \
	file://0008-RGB2FB.patch \
"

CFLAGS += "-D_GNU_SOURCE -D__KERNEL_STRICT_NAMES"

do_configure() {
	CC="${CC}" ./configure
}

do_compile() {
	oe_runmake CC="${CC}"
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 fbv ${D}${bindir}

# man
	install -d ${D}${mandir}/man1/
	install -m 0644 fbv.1 ${D}${mandir}/man1/fbv.1
}


SRC_URI[md5sum] = "3e466375b930ec22be44f1041e77b55d"
SRC_URI[sha256sum] = "9b55b9dafd5eb01562060d860e267e309a1876e8ba5ce4d3303484b94129ab3c"
