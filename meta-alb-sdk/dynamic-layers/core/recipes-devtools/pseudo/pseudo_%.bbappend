FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# Add support for extra syscalls and better chroot to support apt-get
# use inside pseudo for target rootfs setups.
#
# Note: pseudo is always built as class-native, where MACHINEOVERRIDES
# is stripped from OVERRIDES. So SRC_URI:ubuntu would not match. We
# must instead gate via a parse-time include — at parse time
# MACHINEOVERRIDES is still set globally (e.g. "ubuntu:s32n79rdb:..."),
# and the included file's SRC_URI += gets baked into the recipe
# metadata that all variants (target, native, nativesdk) inherit.
include ${@'pseudo-patches.inc' if 'ubuntu' in (d.getVar('MACHINEOVERRIDES') or '') else ''}
