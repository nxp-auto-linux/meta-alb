require ${@bb.utils.contains('DISTRO_FEATURES', 'quick-boot', 'recipes-core/systemd/systemd-quick-boot.inc', '', d)}
