FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
	file://0001-qemu-Reinstated-syscall-emulation-through-libc-via-c.patch \
	file://0001-qemu-Fixed-utimensat-for-libc-based-calls.patch \
	file://0001-qemu-Removed-broken-capset-patch-and-cleaned-utimens.patch \
\
	file://0002-qemu-Backport-of-qemu.git-a78b1299f1bbb9608e3e3a36a7.patch \
	file://0003-qemu-Backport-of-qemu.git-9c4bbee9e3b83544257e825663.patch \
	file://0004-qemu-Backport-of-qemu.git-06065c451f10c7ef62cfb575a8.patch \
\
	file://0001-linux-user-getsockopt-syscall-was-not-treating-lengt.patch \
	file://0001-corrected-getsockopt-implementation-to-eliminate-war.patch \
	file://0002-strace-logging-for-dup3-and-seccomp-to-better-debug-.patch \
	file://0003-seccomp-handling-was-not-checked-properly-and-caused.patch \
\
	file://0001-qemu-linux-user-strace-is-now-more-comprehensive-abo.patch \
\
	file://0001-linux-user-mmap.c-Avoid-choosing-NULL-as-start-addre.patch \
	file://0002-linux-user-fix-mmap-munmap-mprotect-mremap-shmat.patch \
	file://0003-linux-user-fix-target_mprotect-target_munmap-error-r.patch \
	file://0004-linux-user-mmap.c-handle-invalid-len-maps-correctly.patch \
	file://0005-linux-user-syscall.c-target_mmap-was-called-with-0-f.patch \
	file://0006-linux-user-elfload.c-Allocate-beyond-the-host-proces.patch \
"

QEMU_TARGETS += "ppc64"
