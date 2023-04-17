# We want to use the image mechanism without filling a rootfs to save
# space and time.
# So we use a simple trick to make do_rootfs nearly nothing.
IMAGE_LINGUAS = ""
IMAGE_INSTALL = ""
PACKAGE_INSTALL = ""
fakeroot python do_rootfs:prepend () {
    d.setVar("IMAGE_PREPROCESS_COMMAND", "")
    d.setVar("IMAGE_POSTPROCESS_COMMAND", "")
}
inherit image
ROOTFS_BOOTSTRAP_INSTALL = ""
KERNELDEPMODDEPEND = ""
KERNEL_DEPLOY_DEPEND = ""
