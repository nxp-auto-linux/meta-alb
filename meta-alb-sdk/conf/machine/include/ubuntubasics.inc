# Ubuntu userland version selection.
#
# The primary/default image is Ubuntu 24.04.4 (matches the poky scarthgap
# toolchain). To build the SECONDARY Ubuntu 22.04 image instead, override this
# in conf/local.conf:
#
#     UBUNTU_TARGET_VERSION = "22.04.1"
#
# Everything version-sensitive below (and in the image recipes) derives from
# UBUNTU_TARGET_VERSION / UBUNTU_TARGET_BASEVERSION, so that single line flips
# the whole stack.
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

# Selects the Yocto (build-time) glibc/binutils versions used for the few
# Yocto-native recipes that get merged into the Ubuntu image. This does NOT
# set the toolchain on the Ubuntu rootfs itself (that is Ubuntu's own .debs).
#
# General rule: the Yocto glibc version should not exceed the Ubuntu glibc
# version, or Yocto-built executables shipped into the rootfs will fail on
# the Ubuntu image. Ideally track the target release's Ubuntu glibc as
# closely as the available recipes allow (the per-release glibc is a-priori
# knowledge). Note the Yocto glibc is not built identically to Ubuntu's; it
# only has to work for the Yocto-built binaries, which is good enough here.
#
#   Ubuntu 24.04: glibc 2.35 -> n/a; glibc 2.39 -> Yocto 2.39% (rule honoured,
#                 matches the poky scarthgap default)
#   Ubuntu 22.04: Ubuntu glibc is 2.35, but see the exception below.
#
# Exception (Ubuntu 22.04 secondary on scarthgap): this BSP is built on poky
# "scarthgap", which only ships glibc 2.39 / binutils 2.42 (no 2.35 / 2.38
# recipes exist here). Ubuntu 22.04 was the kirkstone-matched distro (glibc
# 2.35 / binutils 2.38), so those old pins would dangle on scarthgap. For the
# 22.04 secondary image we therefore deliberately use the scarthgap toolchain
# (2.39 / 2.42), identical to the 24.04 branch, knowingly exceeding the Ubuntu
# 2.35 and breaking the "do not exceed" rule above. The tradeoff: Yocto-built
# *target* ELFs are glibc-2.39 and must NOT be shipped on the 22.04
# (glibc-2.35) rootfs. This is safe because those Yocto-native target packages
# are centrally excluded from the 22.04 variant via UBUNTU_2204_NATIVE_EXCLUDE
# / PACKAGE_EXCLUDE below, and everything else on the rootfs is an Ubuntu
# 22.04 .deb (glibc 2.35). The runtime toolchain (gcc, libc6, libc6-dev, apt)
# is thus fully self-consistent 22.04.

# Single place to keep Yocto-native target userspace packages off the 22.04
# rootfs. Add new native userspace packages here rather than gating each recipe
# line individually. Kernel modules (pfe, sja1110, ...) do not belong here,
# they are independent of the rootfs and must keep installing. PACKAGE_EXCLUDE
# is a no-op for the 24.04 image and for non-Ubuntu machines.
UBUNTU_2204_NATIVE_EXCLUDE ?= "\
    aquantia-firmware-utility \
    optee-client optee-examples optee-test \
    libfci-cli \
"
PACKAGE_EXCLUDE:append = "${@oe.utils.conditional('UBUNTU_TARGET_BASEVERSION', '22.04', ' ${UBUNTU_2204_NATIVE_EXCLUDE}', '', d)}"

GLIBCVERSION = '${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "22.04", "2.39%", \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "24.04", "2.39%", \
    "unsupportedubuntuversion" \
    , d) \
    , d)}'
BINUVERSION='${@ \
    oe.utils.conditional("UBUNTU_TARGET_BASEVERSION", "22.04", "2.42", \
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

# Build-time qemu-user workaround for the Ubuntu 22.04 (jammy, glibc 2.35)
# rootfs. Two of jammy's arm64 maintainer-script helpers fail when
# nativeaptinstall runs the target dpkg/apt under the scarthgap qemu-user:
#
#   - /usr/sbin/ldconfig.real (a static-pie ELF) crashes with SIGSEGV when
#     the libc-bin ldconfig trigger execs it under qemu, aborting the apt run.
#   - /usr/bin/py3compile and /usr/bin/py3clean spawn a nested python3
#     subprocess to probe the ABI tag; that nested exec-under-emulation is
#     unreliable on the jammy interpreter and intermittently fails, aborting
#     the python3 package postinst.
#
# Redirect the affected execs to /bin/true via pseudo's chroot exec
# translation. Both skipped steps (ld.so.cache, .pyc bytecode caches) are
# regenerated natively on the target, so this is safe. glibc 2.39 (24.04)
# does not hit either issue, so this is gated to 22.04 and is a no-op for
# the default 24.04 build.
PSEUDO_CHROOT_XTRANSLATION:append = "${@oe.utils.conditional('UBUNTU_TARGET_BASEVERSION', '22.04', ':ldconfig.real=/bin/true:py3compile=/bin/true:py3clean=/bin/true', '', d)}"


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
