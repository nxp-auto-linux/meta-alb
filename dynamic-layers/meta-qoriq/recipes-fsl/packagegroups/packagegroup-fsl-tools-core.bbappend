# No DPAA on S32V!
DPAA_PKGS_s32v = ""

# Incompatibility of pkc-host to the kernel of S32V.
# While the S32V kernel version differs from the qoriq version,
# we need to specifically encode compatibility to exclude S32V.
# We do this by hacking the compatibility in ugly ways
RDEPENDS_${PN}_remove_s32v = "pkc-host"

RDEPENDS_${PN}_append_ls2080abluebox = " \
    odp \
    devmem2 \
    ofp \
    fio \
    restool \
"
RDEPENDS_${PN}_append_ls2084a = " \
    devmem2 \
    ofp \
    fio \
    restool \
"

