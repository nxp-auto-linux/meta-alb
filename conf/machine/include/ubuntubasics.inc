# Downconfigure Yocto environment to not pull in local things
IMAGE_LINGUAS = ""
SDK_RDEPENDS = ""
ROOTFS_PKGMANAGE_BOOTSTRAP = ""
POKY_DEFAULT_EXTRA_RDEPENDS = ""
INITRAMFS_IMAGE = ""

# To run pseudo in proper environments, we need to have chroot
# available. As there is no simple chroot-native, we take it from
# the host.
HOSTTOOLS += "chroot"

# This is to be able to pull in the prebuilt Ubuntu rootfs
MACHINEOVERRIDES =. "ubuntu:"

DISTRO_FEATURES_remove = "sysvinit bluez5"
DISTRO_FEATURES_append = " systemd"
DISTRO_FEATURES_append += "docker"
VIRTUAL-RUNTIME_init_manager = "systemd"
IMAGE_FSTYPES_remove = "cpio.gz tar.gz"

# Turns out that we want the Yocto recipes as default provider, but
# then we must ensure they do not get deployed to avoid pollution of
# the target rootfs.
# Rationale is that Yocto builds need to be internally consistent,
# and we need the sysroots info for Yocto recipes, but the Ubuntu rootfs
# provides things, too. We can't use that for building though, so Yocto
# packages conflicting must not reach the package manager.
# Whenever a Yocto package is installed, all dependencies which are
# to be provided by Ubuntu must be added to the list. Check
# recipe-depends.dot from bitbake -g.
# The respective Ubuntu image must then install the Ubuntu version of
# the recipe.
require conf/machine/include/nodistropackage.inc
DISTRO_NEVER_WRITE_PACKAGE = "\
	update-alternatives \
	bash \
	base-files \
	bc \
	bzip2 \
	coreutils \
	db \
	dpkg \
	glibc \
	glibc-locale \
	gcc-runtime \
	initscripts \
	libffi \
	libgcc \
	liblzma \
	libpam \
	ncurses \
	netbase \
	opkg-utils \
	perl \
	python3 \
	readline \
	util-linux \
	xz \
	zlib \
"

require conf/machine/include/ubuntutcconf.inc
