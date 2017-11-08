SRC_URI = "http://ftp.debian.org/debian//pool/main/n/netkit-rusers/${BPN}_${PV}.orig.tar.gz;name=archive \
           http://ftp.debian.org/debian//pool/main/n/netkit-rusers/${BPN}_${PV}-8.diff.gz;name=patch8 \
           file://rpc.rusersd-Makefile-fix-parallel-build-issue.patch \
"
