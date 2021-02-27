FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
	file://0001-atf-Support-for-ls2084abbmini.patch \
"

PLATFORM_ls2080abluebox = "ls2088ardb"
PLATFORM_ls2084abluebox = "ls2088ardb"
