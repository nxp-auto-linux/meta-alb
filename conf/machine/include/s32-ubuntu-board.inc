require conf/machine/include/ubuntubasics.inc

DEPENDS_remove_libc-glibc = " glibc-locale"
BOOT_SCRIPTS = ""
DISTRO_FEATURES_append += "docker"
