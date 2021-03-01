FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
	file://0001-atf-Support-for-ls2084abbmini.patch \
"

# What's in a name? That which we call a rose,
# By any other name would smell as sweet.
RCW_FOLDER_ls1043abluebox = "ls1043ardb"
PLATFORM_ls1043abluebox = "ls1043ardb"
RCW_FOLDER_ls1046abluebox = "ls1046ardb"
PLATFORM_ls1046abluebox = "ls1046ardb"

# We have slightly modified custom RCWs here
PLATFORM_ls2080abluebox = "ls2088ardb"
PLATFORM_ls2084abluebox = "ls2088ardb"
