
PREFERRED_VERSION_gmac = "${@bb.utils.contains('PREFERRED_VERSION_linux-s32', '4.14', '0.4.1', '0.4.2', d)}"
# For kernel 4.14 we have a separate gmac recipe; for kernel 4.19, gmac is built with the kernel
GMAC_IMAGE_INSTALL = "${@bb.utils.contains('PREFERRED_VERSION_linux-s32', '4.14', 'gmac', '', d)}"

# Add gmac by default
DISTRO_FEATURES_append += "${@bb.utils.contains('PREFERRED_VERSION_linux-s32', '4.14', ' gmac', '', d)}"
