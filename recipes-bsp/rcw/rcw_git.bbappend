FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

LEGACY_INCLUDE ??= ""
LEGACY_INCLUDE_ls1046abluebox = "rcw_git_legacy.inc"
LEGACY_INCLUDE_ls2 = "rcw_git_legacy.inc"

# Not based on legacy RCW:
BOARD_TARGETS_ls1043abluebox="ls1043ardb"

include ${LEGACY_INCLUDE}
