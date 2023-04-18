FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

BOARD_TARGETS:ls1043abluebox="ls1043ardb"
BOARD_TARGETS:ls1046abluebox="ls1046ardb"
BOARD_TARGETS:lx2160ardb2bluebox="lx2160ardb_rev2"

SRC_URI:append = " \
	file://0001-rcw-Configurations-for-LS2-based-BlueBox-variants.patch \
	file://0001-rcw-Configurations-for-lx2160abluebox3.patch \
"
