# Enable Xen and add Xen Packages
XEN_IMAGE_MINIMAL = "${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'recipes-extended/images/xen-image-minimal.bb', '', d)}"
include ${XEN_IMAGE_MINIMAL}
IMAGE_INSTALL += "${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'zlib-dev xen-xenstat xen-xenmon', '', d)}"

