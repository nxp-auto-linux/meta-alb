# Downconfigure Yocto environment to not pull in local things
IMAGE_LINGUAS = ""
SDK_RDEPENDS = ""
POKY_DEFAULT_EXTRA_RDEPENDS = ""
INITRAMFS_IMAGE = ""

# To run pseudo in proper environments, we need to have chroot
# available. As there is no simple chroot-native, we take it from
# the host.
HOSTTOOLS += "chroot"

# This is to be able to pull in the prebuilt Ubuntu rootfs
MACHINEOVERRIDES =. "ubuntu:"

DISTRO_FEATURES_remove = "sysvinit"
DISTRO_FEATURES_append = " systemd"
DISTRO_FEATURES_append += "docker"
VIRTUAL-RUNTIME_init_manager = "systemd"

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
	alsa-conf \
	alsa-lib \
	update-alternatives \
	bash \
	base-files \
	bc \
	bluez5 \
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
	readline \
	util-linux \
	xz \
	zlib \
"

PREFERRED_PROVIDER_bzip2="ubuntu-base"
PREFERRED_PROVIDER_python3="ubuntu-base"

# Avoid the warning reporting for recipes that RDEPENDS on ubuntu-base
# The workaround is to INSANE_SKIP build-deps for these recipes
require conf/machine/include/set_insane_skip.inc
SET_INSANE_SKIP_VALUE = " build-deps "
SET_INSANE_SKIP_LIST = "\
	flex \
	sqlite3 \
	db-bin \
	ncurses \
	ncurses-libticw \
	ncurses-libpanel \
	ncurses-libncurses \
	ncurses-libncursesw \
	ncurses-libpanelw \
	ncurses-libformw \
	ncurses-libtic \
	ncurses-libmenu \
	ncurses-libmenuw \
	ncurses-libform \
	ncurses-tools \
	openssl \
	python3 \
	python3-ctypes \
	python3-misc \
	python3-sqlite3 \
	python3-curses \
"

require conf/machine/include/ubuntutcconf.inc
