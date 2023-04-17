# For some reason, kirkstone meta-freescale and meta-qoriq do not seem
# to agree
DEPENDS:remove = "cst-native"
DEPENDS:append = " qoriq-cst-native"

# The following patches are BlueBox specific. They unfortunately
# cannot just reside in meta-alb and have to be in the dynamic-layers
# which are usually meant for generic fixes only. Reason being that
# for S32G meta-qoriq is not included and then the bbappends cause
# an error.
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://0001-qoriq-atf-Support-for-ls2084abbmini.patch \
"

# What's in a name? That which we call a rose,
# By any other name would smell as sweet.
RCW_FOLDER:ls1043abluebox = "ls1043ardb"
PLATFORM:ls1043abluebox = "ls1043ardb"
RCW_FOLDER:ls1046abluebox = "ls1046ardb"
PLATFORM:ls1046abluebox = "ls1046ardb"

# We have slightly modified custom RCWs here
PLATFORM:ls2084abluebox = "ls2088ardb"

