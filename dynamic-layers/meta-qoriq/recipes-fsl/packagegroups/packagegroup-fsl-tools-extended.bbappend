# Aligning BlueBox with the SDK
RDEPENDS:${PN}:append:ls1043abluebox = " \
    dpdk \
"
RDEPENDS:${PN}:append:ls1046abluebox = " \
    dpdk \
"
RDEPENDS:${PN}:append:ls2084a = " \
    dpdk \
    odp \
"

RDEPENDS:${PN}:remove:ls2084a = "aiopsl"
