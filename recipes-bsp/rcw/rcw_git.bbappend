FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

LEGACY_INCLUDE ??= ""
LEGACY_INCLUDE_ls2 = "rcw_git_legacy.inc"

include ${LEGACY_INCLUDE}
