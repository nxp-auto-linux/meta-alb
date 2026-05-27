
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

# How to prepare the kernel source tree on the rootfs:
#   ""     - run 'make scripts modules_prepare' in QEMU+pseudo chroot during do_rootfs
#   "1"    - install a systemd oneshot that runs it on target at first boot
#   "skip" - do not run modules_prepare at all; source tree is shipped as-is.
#            Use this when on-target out-of-tree module builds are not supported;
#            cross-compile modules on the build host against
#            ${STAGING_KERNEL_BUILDDIR} instead.
KERNEL_DEVSRC_DEFER_TO_SYSTEMD ?= ""

fakeroot do_aptget_user_update:append() {

	if [ "${KERNEL_DEVSRC_DEFER_TO_SYSTEMD}" = "skip" ]; then
		:
	elif [ -e "${KERNEL_CHROOT_DIR}/${KERNEL_SRC_PATH}" ]; then
		if [ -e "${KERNEL_CHROOT_DIR}${root_prefix}/lib/systemd/system" -a "${KERNEL_DEVSRC_DEFER_TO_SYSTEMD}" = "1" ]; then
			# Only modules_prepare is needed; it pulls 'scripts' as a
			# Makefile dependency. Running both separately causes a
			# Text-file-busy race on scripts/basic/fixdep when systemd
			# starts the two units in parallel.
			aptget_install_oneshot_service "makemodules" "Build kernel modules_prepare" "/usr/bin/make -j8 -C ${KERNEL_SRC_PATH} modules_prepare"
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
