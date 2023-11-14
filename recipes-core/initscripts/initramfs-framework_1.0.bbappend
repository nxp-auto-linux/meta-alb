require ${@bb.utils.contains('DISTRO_FEATURES', 'root-hash-sign', 'initramfs-framework.inc', '', d)}
