# Enable Xen and add Xen Packages
include recipes-extended/images/xen-image-minimal.bb

IMAGE_INSTALL += "zlib-dev xen-tools-xenstat xen-tools-xenmon"

# Enable deployment of Xen binary to fsl-image-auto.sdcard image
SDCARDIMAGE_BOOT_EXTRA1 = "xen"
SDCARDIMAGE_BOOT_EXTRA1_FILE = "xen"

