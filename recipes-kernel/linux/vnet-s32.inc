# virtual ethernet support for linux kernel

# add sources for virtual ethernet over PCIe
VNET_URL ?= "git://source.codeaurora.org/external/autobsps32/vnet;protocol=https"
VNET_BRANCH ?= "${RELEASE_BASE}"

MIN_VER =  "${@oe.utils.trim_version("${PV}", 2)}"
