
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

fakeroot do_aptget_user_update_append() {

	if [ -e "${KERNEL_CHROOT_DIR}/${KERNEL_SRC_PATH}" ]; then
		if [ -e "${KERNEL_CHROOT_DIR}${root_prefix}/lib/systemd/system" ]; then
			aptget_install_oneshot_service "makescripts" "Build kernel scripts" "/usr/bin/make V=1 -C ${KERNEL_SRC_PATH} scripts"
		else
			# Very slooooooow fallback
			/usr/bin/yes "" | ARCH=${DEBIAN_TARGET_ARCH} chroot "${KERNEL_CHROOT_DIR}" /usr/bin/make V=1 -C "${KERNEL_SRC_PATH}" scripts
		fi
	fi

}

IMAGE_INSTALL += "\
	kernel-devsrc \
"
