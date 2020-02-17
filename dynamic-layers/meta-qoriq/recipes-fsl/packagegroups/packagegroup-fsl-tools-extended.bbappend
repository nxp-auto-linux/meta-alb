# Aligning BlueBox with the SDK
RDEPENDS_${PN}_append_ls1043abluebox = " \
    dpdk \
"
RDEPENDS_${PN}_append_ls1046abluebox = " \
    dpdk \
"
RDEPENDS_${PN}_append_ls2080abluebox = " \
    dpdk \
    odp \
"
RDEPENDS_${PN}_append_ls2084a = " \
    dpdk \
    odp \
"

RDEPENDS_${PN}_append_ls2080a = " \
    dpdk \
    odp \
"
RDEPENDS_${PN}_remove_ls2084a = "aiopsl"
