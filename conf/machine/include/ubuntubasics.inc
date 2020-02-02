# We have recipies for:
# - 16.04.5
# - 18.04.1
UBUNTU_TARGET_VERSION ?= "16.04.5"

UBUNTU_TARGET_BASEVERSION ?= "${@os.path.splitext(d.getVar('UBUNTU_TARGET_VERSION', True))[0]}"
UBUNTU_TARGET_SUBVERSION ?= "${@d.getVar('UBUNTU_TARGET_VERSION', True).rsplit('.')[2]}"
PREFERRED_VERSION_ubuntu-base = "${UBUNTU_TARGET_VERSION}"

# I am sure there are smarter ways to map the architecture. This works
# initially. FIX
UBUNTU_TARGET_ARCH="${@d.getVar('TARGET_ARCH', True).replace("aarch64", "arm64")}"

# Downconfigure Yocto environment to not pull in local things
IMAGE_LINGUAS = ""
POKY_DEFAULT_EXTRA_RDEPENDS_remove = "packagegroup-core-boot"
INITRAMFS_IMAGE = ""

# The Yocto glibc version must not exceed the Ubuntu glibc version
# or Yocto created executables will fail on the Ubuntu image.
# We want to be as close as possible to the respective Ubuntu glibc
# to avoid compatibility issues.
# The glibc version for a given Ubuntu release is a priori knowledge,
# so the suitable Yocto version can be selected here.
# TODO: This is not really functional yet
#GLIBCVERSION = '${@ \
#    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "16.04", "linaro-2.20", \
#    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "18.04", "linaro-2.20", \
#    "unsupportedubuntuversion" \
#    , d) \
#    , d)}'

# This is to be able to pull in the prebuilt Ubuntu rootfs
MACHINEOVERRIDES =. "ubuntu:"

# When creating images, we would like to preserve any extended
# attributes that are part of our rootfs
IMAGE_CMD_TAR = "tar --xattrs --xattrs-include=*"
do_image_tar[depends] += "tar-replacement-native:do_populate_sysroot"
EXTRANATIVEPATH += "tar-native"

# We influence the setup to match Ubuntu more properly as otherwise
# Yocto behavior may conflict with the Ubuntu rootfs.
INIT_MANAGER = "systemd"

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
DISTRO_NEVER_WRITE_PACKAGE_class-target += "\
	alsa-conf \
	alsa-lib \
	update-alternatives \
	bash \
	base-files \
	bc \
	bison \
	bluez5 \
	bzip2 \
	coreutils \
	db \
	diffutils \
	dpkg \
	flex \
	glibc \
	glibc-locale \
	gcc-runtime \
	gdbm \
	initscripts \
	libffi \
	libgcc \
	liblzma \
	libpam \
	libsqlite3 \
	m4 \
	ncurses \
	netbase \
	opkg-utils \
	perl \
	python3 \
	qemuwrapper-cross \
	sqlite3 \
	readline \
	util-linux \
	xz \
	zlib \
"
# All the runtime above should come from ubuntu-base,
# but only for the target, not for native elements.
DISTRO_DEFAULT_RPROVIDER_class-target = "ubuntu-base"

require conf/machine/include/ubuntutcconf.inc
