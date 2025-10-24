IMAGE_FSTYPES += "cpio.gz"

DISTRO_FEATURES:append = " virtio"

INIT_MANAGER = "${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'systemd', 'mdev-busybox', d)}"
