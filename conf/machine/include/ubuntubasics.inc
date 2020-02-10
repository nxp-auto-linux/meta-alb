# We currently support recipies for:
# - 16.04.5
# - 18.04.1
#UBUNTU_TARGET_VERSION ?= "16.04.5"
UBUNTU_TARGET_VERSION ?= "18.04.3"

UBUNTU_TARGET_BASEVERSION ?= "${@os.path.splitext(d.getVar('UBUNTU_TARGET_VERSION', True))[0]}"
UBUNTU_TARGET_SUBVERSION ?= "${@d.getVar('UBUNTU_TARGET_VERSION', True).rsplit('.')[2]}"
PREFERRED_VERSION_ubuntu-base = "${UBUNTU_TARGET_VERSION}"

# I am sure there are smarter ways to map the architecture. This works
# initially. FIX
UBUNTU_TARGET_ARCH="${@d.getVar('TRANSLATED_TARGET_ARCH', True).replace("aarch64", "arm64")}"

# Downconfigure Yocto environment to not pull in local things
# We do not want any default post intercepts or special default
# packages added.
IMAGE_LINGUAS = ""
POKY_DEFAULT_EXTRA_RDEPENDS_remove = "packagegroup-core-boot"
INITRAMFS_IMAGE = ""
POSTINST_INTERCEPTS_DIR = "<--InVaLiD-->"

# The Yocto glibc version must not exceed the Ubuntu glibc version
# or Yocto-created executables will fail on the Ubuntu image.
# We want to be as close as possible to the respective Ubuntu glibc
# version to avoid compatibility issues.
# The glibc version for a given Ubuntu release is a-priori knowledge,
# so the suitable Yocto version can be selected here.
# Note that the glibc is not compiled in Ubuntu identical fashion.
# It remains a glibc that works for Yocto, which is good enough for
# our purposes.
# Ubuntu 16.04: max glibc 2.23...
# Ubuntu 18.04: max glibc 2.27...
# Ubuntu 19.x: max glibc 2.29...
GLIBCVERSION = '${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "16.04", "2.23", \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "18.04", "2.27", \
    "unsupportedubuntuversion" \
    , d) \
    , d)}'
PREFERRED_VERSION_cross-localedef-native = "${GLIBCVERSION}"

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
    dtc \
	flex \
	glibc \
	glibc-locale \
	gcc-runtime \
	gdbm \
	initscripts \
    libc6 \
    libc6-utils \
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

# We have some packages which we do not want to have in a target
# image because they are available as Ubuntu-native packages.
# Given that we can't set a PREFERRED_RPROVIDER in an image recipe
# we need to isolate those packages globally.
DISTRO_NEVER_WRITE_PACKAGE_class-target += "\
    openssl-conf \
    openssl-dev \
    openssl-qoriq \
    openssl-qoriq-dev \
"

# All the runtime above should come from ubuntu-base or a derived image
# but only for the target, not for native elements.
# The trick is that we need to retarget everything to one recipe because
# at configuration time we cannot predict algorithmically which derived
# images would add the final apt packages. Any "missing" dependencies
# would only show up then at image build time.
DISTRO_DEFAULT_RPROVIDER_class-target = "ubuntu-base (= ${UBUNTU_TARGET_VERSION})"

#require conf/machine/include/ubuntutcconf.inc
