#QA Issue: numactl-ptest requires /bin/bash, but no providers in its RDEPENDS [file-rdeps]
RDEPENDS_${PN}-ptest = "bash"

SRC_URI = "ftp://ftp.ru.debian.org/gentoo-distfiles/distfiles/${BPN}-${PV}.tar.gz \
	   file://fix-null-pointer.patch \
	   file://Fix-the-test-output-format.patch \
	   file://Makefile \
	   file://run-ptest \
          "
