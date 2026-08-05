DEPENDS:append:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' xen-tools', '', d)}"

# Xen-specific QEMU configuration (build dependencies + trimming)
require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'qemu-xen-minimal.inc', '', d)}
