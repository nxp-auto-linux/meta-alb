# Copyright 2022 NXP

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
#
# The original default is too simple to be very useful in practice on the target.
# This is a convenience recipe only
#
SRC_URI += "\
    file://fstab \
    file://share/dot.bashrc \
"
