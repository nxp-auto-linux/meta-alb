FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

BOARD_TARGETS_ls1043abluebox="ls1043ardb"
BOARD_TARGETS_ls1046abluebox="ls1046ardb"

SRC_URI_append += "\
	file://0001-rcw-Configurations-for-LS2-based-BlueBox-variants.patch \
"
