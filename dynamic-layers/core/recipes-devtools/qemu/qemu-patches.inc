SRC_URI += "\
	file://0001-qemu-Reinstated-syscall-emulation-through-libc.patch \
	file://0002-qemu-user-corrected-getsockopt-implementation.patch \
	file://0003-qemu-user-seccomp-handling-could-crash.patch \
	file://0004-qemu-user-SO_PEERGROUPS-was-not-emulated.patch \
	file://0005-qemu-user-syscall.c-target_mmap-was-called-with-bad-.patch \
	file://0006-qemu-user-builtin-strace-much-more-comprehensive.patch \
	file://0007-qemu-user-strace-didn-t-print-faccessat-correctly.patch \
\
	file://0001-qemu-Added-strace-updates-for-faccessat2-pipe-pipe2-.patch \
\
	file://0001-qemu-Run-statx-and-renameat2-optionall-through-libc.patch \
"
