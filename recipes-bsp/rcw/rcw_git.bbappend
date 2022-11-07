FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

BOARD_TARGETS:ls1043abluebox="ls1043ardb"
BOARD_TARGETS:ls1046abluebox="ls1046ardb"

SRC_URI:append = " \
	file://0001-rcw-Configurations-for-LS2-based-BlueBox-variants.patch \
"
