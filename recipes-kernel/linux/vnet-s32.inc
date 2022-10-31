# virtual ethernet support for linux kernel

# add sources for virtual ethernet over PCIe
VNET_URL ?= "https://github.com/nxp-auto-linux/vnet;protocol=https"
VNET_BRANCH ?= "${RELEASE_BASE}"

MIN_VER =  "${@oe.utils.trim_version("${PV}", 2)}"
