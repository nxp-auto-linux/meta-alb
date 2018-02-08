FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
	file://0001-qemu-Reinstated-syscall-emulation-through-libc-via-c.patch \
	file://0001-qemu-Fixed-utimensat-for-libc-based-calls.patch \
	file://0001-qemu-Removed-broken-capset-patch-and-cleaned-utimens.patch \
\
	file://0001-qemu-Verbatim-backport-of-qemu.git-024949caf32805f4c.patch \
	file://0002-qemu-Backport-of-qemu.git-a78b1299f1bbb9608e3e3a36a7.patch \
	file://0003-qemu-Backport-of-qemu.git-9c4bbee9e3b83544257e825663.patch \
	file://0004-qemu-Backport-of-qemu.git-06065c451f10c7ef62cfb575a8.patch \
"
