# We currently support recipies for:
# - 24.04.4
#UBUNTU_TARGET_VERSION ?= "22.04.1"
UBUNTU_TARGET_VERSION ?= "24.04.4"

UBUNTU_TARGET_MAJORVERSION ?= "${@d.getVar('UBUNTU_TARGET_VERSION', True).rsplit('.')[0]}"
UBUNTU_TARGET_MINORVERSION ?= "${@d.getVar('UBUNTU_TARGET_VERSION', True).rsplit('.')[1]}"
UBUNTU_TARGET_SUBVERSION ?= "${@d.getVar('UBUNTU_TARGET_VERSION', True).rsplit('.')[2]}"
UBUNTU_TARGET_BASEVERSION ?= "${UBUNTU_TARGET_MAJORVERSION}.${UBUNTU_TARGET_MINORVERSION}"
REQUIRED_VERSION_ubuntu-base = "${UBUNTU_TARGET_VERSION}"

# I am sure there are smarter ways to map the architecture. This works
# initially. FIX
UBUNTU_TARGET_ARCH="${@d.getVar('TRANSLATED_TARGET_ARCH', True).replace("aarch64", "arm64")}"

# Downconfigure Yocto environment to not pull in local things
# We do not want any default post intercepts or special default
# packages added.
IMAGE_LINGUAS = ""
POKY_DEFAULT_EXTRA_RDEPENDS:remove = "packagegroup-core-boot"
INITRAMFS_IMAGE = ""
POSTINST_INTERCEPTS_DIR = "<--InVaLiD-->"

# We really should also have this line active, but recipe deficiencies
# make this a non-option currently
#FILESYSTEM_PERMS_TABLES = ""

# Ubuntu does not use /var/volatile
VOLATILE_LOG_DIR = "no"

# Removal of flash image filesystem support
# for Ubuntu images due to an issue regarding 'cpio'
# utility when seeking on large files (> 2 GB).
# As a Ubuntu based rootfs can easily go beyond 2GB and are even in
# the smallest form larger than what is good for a boot flash, we
# do not generate a .flashimage with Ubuntu.
IMAGE_FSTYPES:remove = "flashimage"
IMAGE_TYPES_MASKED += "cpio cpio.gz cpio.gz.u-boot"

# The Yocto glibc version must not exceed the Ubuntu glibc version
# or Yocto-created executables will fail on the Ubuntu image.
# We want to be as close as possible to the respective Ubuntu glibc
# version to avoid compatibility issues.
# The glibc version for a given Ubuntu release is a-priori knowledge,
# so the suitable Yocto version can be selected here.
# Note that the glibc is not compiled in Ubuntu identical fashion.
# It remains a glibc that works for Yocto, which is good enough for
# our purposes.
# Ubuntu 22.x: max glibc 2.35...
# Ubuntu 24.x: max glibc 2.39... (matches poky scarthgap default)
GLIBCVERSION = '${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "22.04", "2.35", \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "24.04", "2.39%", \
    "unsupportedubuntuversion" \
    , d) \
    , d)}'
BINUVERSION='${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "22.04", "2.38", \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "24.04", "2.42", \
    "unsupportedubuntuversion" \
    , d) \
    , d)}'

# We need to understand the python version in Ubuntu for dependency
# management
APTGET_PYTHON_VER = '${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "22.04", "3.10", \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "24.04", "3.12", \
    "unsupportedubuntuversion" \
    , d) \
    , d)}'


# More recent Ubuntu versions work with symlinks for the base
# directories. We can't refer to symlink based files
DISTRO_FEATURES:append = '${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "22.04", " usrmerge", \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "24.04", " usrmerge", \
   "unsupportedubuntuversion" \
    , d) \
    , d)}'

# Suppress harmless do_rootfs log_check matches caused by installing
# certain packages inside the pseudo+qemu chroot:
#
#   - NetworkManager postinst calls nmcli to reload connections via
#     D-Bus, but the NM daemon isn't running in the chroot. Noble's
#     NM 1.46 prints
#         "Error: Could not create NMClient object: ..."
#         "WARNING: NetworkManager could not reload connections ..."
#     which trips Yocto's rpm-rootfs error/warning regexes. On jammy
#     (NM 1.36) the same failure logs softer text that doesn't match.
#
#   - logger(1) prints
#         "logger: send message failed: Operation not permitted"
#     because syslogd isn't running in the chroot during install.
#
# All of these are post-install warnings about chroot-only limitations,
# not real failures: on the booted target each service starts normally.
#
# IMAGE_LOG_CHECK_EXCLUDES values are space-separated regexes; we use
# \s+ for whitespace since a literal space would be treated as a
# token separator.
IMAGE_LOG_CHECK_EXCLUDES = "Could\s+not\s+create\s+NMClient \
                            NetworkManager\s+could\s+not\s+reload \
                            logger:\s+send\s+message\s+failed"

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

# DISTRO_NEVER_WRITE_PACKAGE format:
# <package name>[:[packagelist]:[preferred rprovider]:[virt-runtime]]
# Where:
#     package name: recipe name. Mandatory parameter
# Optional parameters:
#     packagelist:  list of comma-separated package names
#     rprovider:    specify the preferred rprovider if different than default
#                   (e.g. ubuntu-base)
#     virt-runtime: 0/1:disable(0) or enable(1) overwritting VIRTUAL-RUNTIME
#                   with preferred rprovider. Default is (if not specified)
#                   disable. This is required for pkgs that are directly
#                   referrenced with VIRTUAL-RUNTIME (e.g. update-alternatives)

DISTRO_NEVER_WRITE_PACKAGE:class-target += "\
    alsa-conf \
    alsa-lib \
    update-alternatives:::1 \
    bash \
    base-files \
    base-passwd \
    bc \
    bison \
    bluez5 \
    busybox \
    bzip2 \
    ca-certificates \
    coreutils \
    db \
    dbus \
    diffutils \
    dpkg \
    dtc \
    e2fsprogs \
    ethtool \
    file \
    findutils \
    flex \
    gawk \
    gcc:gcc,gcc-plugins \
    gcc-runtime \
    gdbm \
    initscripts \
    iproute2 \
    iproute2-devlink \
    iputils-ping \
    kmod \
    libc6 \
    libc6-utils \
    libffi \
    libgcc \
    liblzma \
    libmpc:libmpc,libmpc-dev \
    libnsl2 \
    libpam \
    libpam-runtime \
    libsqlite3 \
    libxml2 \
    m4 \
    ncurses \
    ncurses-terminfo-base \
    netbase \
    net-tools \
    opkg-utils \
    perl \
    procps \
    python3 \
    python3-core \
    qemuwrapper-cross \
    shared-mime-info \
    sqlite3 \
    tar \
    readline \
    util-linux \
    xz \
    zlib \
"

# glibc has many subpackages, some of them cause a direct conflict
# with Ubuntu
DISTRO_NEVER_WRITE_PACKAGE:class-target += "\
    glibc \
    glibc-locale \
    libsegfault \
"

# We have some packages which we do not want to have in a target
# image because they are available as Ubuntu-native packages.
# Given that we can't set a PREFERRED_RPROVIDER in an image recipe
# we need to isolate those packages globally.
DISTRO_NEVER_WRITE_PACKAGE:class-target += "\
    openssl \
    openssl-bin \
    openssl-conf \
    openssl-dev \
    openssl-qoriq \
    openssl-qoriq-bin \
    openssl-qoriq-conf \
    openssl-qoriq-dev \
"

# All the runtime above should come from ubuntu-base or a derived image
# but only for the target, not for native elements.
# The trick is that we need to retarget everything to one recipe because
# at configuration time we cannot predict algorithmically which derived
# images would add the final apt packages. Any "missing" dependencies
# would only show up then at image build time.
DISTRO_DEFAULT_RPROVIDER:class-target = "ubuntu-base (= ${UBUNTU_TARGET_VERSION})"

#require conf/machine/include/ubuntutcconf.inc
