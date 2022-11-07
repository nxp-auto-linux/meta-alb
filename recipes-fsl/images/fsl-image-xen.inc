# Enable Xen and add Xen Packages
include recipes-extended/images/xen-image-minimal.bb

# SystemD needs CONFIG_XEN_GNTDEV as built-in instead of module
IMAGE_INSTALL:remove = "kernel-module-xen-gntdev"

IMAGE_INSTALL += "zlib-dev xen-tools-xenstat xen-tools-xenmon"

# Enable deployment of DomU rootfs to EXT partition
inherit fsl-rootfsimage
DOM0LESS_ROOTFS_IMAGE = "${DOM0LESS_ROOTFS}-${MACHINE}.cpio.gz"
IMAGE_ROOTFS_IMAGELIST = " \
    ${@bb.utils.contains('XEN_EXAMPLE', 'xen-examples-dom0less', '${DOM0LESS_ROOTFS_IMAGE}', \
       bb.utils.contains('XEN_EXAMPLE', 'xen-examples-dom0less-passthrough', '${DOM0LESS_ROOTFS_IMAGE}', \
       bb.utils.contains('XEN_EXAMPLE', 'xen-examples-dom0less-passthrough-gmac', '${DOM0LESS_ROOTFS_IMAGE}', \
       bb.utils.contains('XEN_EXAMPLE', 'xen-examples-dom0less-passthrough-pfe', '${DOM0LESS_ROOTFS_IMAGE}', '', d), d), d), d)} \
"

# Install binaries needed by xen example on EXT partition
IMAGE_INSTALL += "${XEN_EXAMPLE}"
