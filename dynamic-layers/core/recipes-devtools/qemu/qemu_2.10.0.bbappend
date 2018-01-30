FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
	file://0001-qemu-Reinstated-syscall-emulation-through-libc-via-c.patch \
	file://0001-qemu-Fixed-utimensat-for-libc-based-calls.patch \
	file://0001-qemu-Removed-broken-capset-patch-and-cleaned-utimens.patch \
"
