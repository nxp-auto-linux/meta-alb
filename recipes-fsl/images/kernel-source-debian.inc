
inherit linux-kernel-base

KERNEL_VERSION_FULL = "${@get_kernelversion_file('${STAGING_KERNEL_BUILDDIR}')}"
KERNEL_VERSION = "${@'${KERNEL_VERSION_FULL}'.split('+')[0]}"

# Parent scripts may override the path to the root filesystem
KERNEL_CHROOT_DIR ?= "${APTGET_CHROOT_DIR}"

# set deploy location for Ubuntu to match Ubuntu's convention for kernel source dir 
KERNEL_SOURCE_DIR = "${KERNEL_VERSION}"
KERNEL_SRC_PATH = "/usr/src/kernel"

# I am sure there are smarter ways to map the architecture. This works
# initially. FIX
DEBIAN_TARGET_ARCH="${@d.getVar('TRANSLATED_TARGET_ARCH', True).replace("aarch64", "arm64")}"

# Ensure minimum build dependencies are installed for target builds
APTGET_EXTRA_PACKAGES += "coreutils make gcc bison flex mawk libssl-dev"

# Build the kernel scripts and modules_prepare in QEMU/pseudo (default) or
# build them in deferred systemd service at first boot (set it to "1")
KERNEL_DEVSRC_DEFER_TO_SYSTEMD ?= ""

fakeroot do_aptget_user_update:append() {

	if [ -e "${KERNEL_CHROOT_DIR}/${KERNEL_SRC_PATH}" ]; then
		if [ -e "${KERNEL_CHROOT_DIR}${root_prefix}/lib/systemd/system" -a "${KERNEL_DEVSRC_DEFER_TO_SYSTEMD}" = "1" ]; then
			aptget_install_oneshot_service "makescripts" "Build kernel scripts" "/usr/bin/make V=1 -C ${KERNEL_SRC_PATH} scripts"
			aptget_install_oneshot_service "makemodules" "Build kernel modules_prepare" "/usr/bin/make -C ${KERNEL_SRC_PATH} modules_prepare"
		else
			PRECOMPILE_SCRIPTS_ERROR="0"
			(/usr/bin/yes "" | ARCH=${DEBIAN_TARGET_ARCH} chroot "${KERNEL_CHROOT_DIR}" /usr/bin/make -C "${KERNEL_SRC_PATH}" scripts modules_prepare ${PARALLEL_MAKE}) || PRECOMPILE_SCRIPTS_ERROR="1"
			if [ "x$PRECOMPILE_SCRIPTS_ERROR" = "x1" ]; then
				bbwarn "Scripts from kernel source tree failed to pre-compile. Please consider building them manually on target."
			fi
		fi
	fi

}

IMAGE_INSTALL += "\
	kernel-devsrc \
"
