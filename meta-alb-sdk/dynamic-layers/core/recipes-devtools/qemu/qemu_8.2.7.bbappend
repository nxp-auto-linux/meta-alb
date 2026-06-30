DEPENDS:append:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' xen-tools', '', d)}"
