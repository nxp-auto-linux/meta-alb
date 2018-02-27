# We want to use the image mechanism without filling a rootfs to save
# space and time.
# So we use a simple trick to make do_rootfs nearly nothing.
LINGUAS_INSTALL = ""
IMAGE_INSTALL = ""
fakeroot python do_rootfs_prepend () {
    d.setVar("PACKAGE_INSTALL", "")
    d.setVar("IMAGE_PREPROCESS_COMMAND", "")
    d.setVar("IMAGE_POSTPROCESS_COMMAND", "")
}
inherit image
