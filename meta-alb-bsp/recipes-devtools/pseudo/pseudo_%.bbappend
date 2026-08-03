FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
    file://0001-pseudo_client-step-fully-past-pseudo-own-fds.patch \
    file://0002-pseudo_client-step-around-pseudo-own-fds-closefrom.patch \
    file://0003-openat-register-O_PATH-fds-so-dirfd-is-tracked.patch \
"
