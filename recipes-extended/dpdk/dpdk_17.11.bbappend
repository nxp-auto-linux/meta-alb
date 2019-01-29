FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://0001-dpdk-extras-Added-support-for-LS2084A.patch \
    file://0001-dpdk-Enable-PMD_PCAP-for-pdump-usage.patch \
"
DEPENDS += "libpcap"

COMPATIBLE_MACHINE_append = "|(ls2080a|ls2084a)"
