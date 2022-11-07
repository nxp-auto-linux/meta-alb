FILESEXTRAPATHS:prepend := "${THISDIR}/odp:"
SRC_URI += "file://0001-kni_net-add-required-include-file-for-signal_pending.patch"

COMPATIBLE_MACHINE:append = "|(ls2084a)"
