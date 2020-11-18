SUMMARY = "A prebuilt Ubuntu Base image as baseline for custom work"
require ubuntu-license.inc
SECTION = "devel"

# Ubuntu 18.04.5
SRC_URI[sha256sum] = "9327cf905e818c38ba04605e40fbe11ac6548537786dc12936ca5819f8a563ad"

require ubuntu-base.inc

# There are some basic differences between different Ubuntu versions.
# We try not to address them in the generic recipe
APTGET_EXTRA_PACKAGES += "libffi6"

# We should not have a single PROVIDES entry as this package
# does not provide anything for build time of any other package!
# PROVIDES += ""

# This is the installed package list as found in log_do_install.
# Minor edits have been done to remove an architecture suffix.
APTGET_RPROVIDES += " \
adduser apt apt-transport-https apt-utils base-files base-passwd bash \
bc bison bsdutils busybox bzip2 ca-certificates coreutils cron dash \
db5.3-doc db5.3-sql-util db5.3-util dbus debconf debianutils \
device-tree-compiler diffutils dirmngr distro-info-data dpkg e2fsprogs \
fdisk file findutils gcc-8-base gir1.2-glib-2.0 gnupg gnupg-l10n \
gnupg-utils gpg gpg-agent gpg-wks-client gpg-wks-server gpgconf gpgsm \
gpgv grep gzip hostname htop init-system-helpers iproute2 iso-codes \
kmod libacl1 libapparmor1 libapt-inst2.0 libapt-pkg5.0 \
libasn1-8-heimdal libassuan0 libatm1 libattr1 libaudit-common libaudit1 \
libbison-dev libblkid1 libbz2-1.0 libc-bin libc6 libcap-ng0 libcom-err2 \
libdb5.3 libdb5.3++ libdb5.3++-dev libdb5.3-dbg libdb5.3-dev \
libdb5.3-java libdb5.3-java-dev libdb5.3-java-jni libdb5.3-sql \
libdb5.3-sql-dev libdb5.3-stl libdb5.3-stl-dev libdb5.3-tcl libdbus-1-3 \
libdebconfclient0 libelf1 libexpat1 libext2fs2 libfdisk1 libffi6 \
libfribidi0 libgcc1 libgcrypt20 libgirepository-1.0-1 libglib2.0-0 \
libglib2.0-data libgmp10 libgnutls30 libgpg-error0 libgssapi3-heimdal \
libhcrypto4-heimdal libheimbase1-heimdal libheimntlm0-heimdal \
libhogweed4 libhx509-5-heimdal libicu60 libidn2-0 libkmod2 \
libkrb5-26-heimdal libksba8 libldap-2.4-2 libldap-common liblz4-1 \
liblzma5 libmagic-mgc libmagic1 libmnl0 libmount1 libmpdec2 libncurses5 \
libncursesw5 libnettle6 libnewt0.52 libnpth0 libnss-db libp11-kit0 \
libpam-modules libpam-modules-bin libpam-runtime libpam0g libpcre3 \
libpopt0 libprocps6 libpython-stdlib libpython2.7-minimal \
libpython2.7-stdlib libpython3-stdlib libpython3.6-minimal \
libpython3.6-stdlib libreadline7 libroken18-heimdal libsasl2-2 \
libsasl2-modules libsasl2-modules-db libseccomp2 libselinux1 \
libsemanage-common libsemanage1 libsepol1 libsigsegv2 libslang2 \
libsmartcols1 libsqlite3-0 libss2 libssl-dev libssl1.1 libstdc++6 \
libsystemd0 libtasn1-6 libtcl8.6 libtinfo5 libudev1 libunistring2 \
libuuid1 libwind0-heimdal libxml2 libxtables12 libzstd1 login lsb-base \
lsb-release m4 mawk mime-support mount ncurses-base ncurses-bin \
net-tools netbase openssl passwd perl perl-base pinentry-curses \
powermgmt-base procps python python-apt-common python-minimal python2.7 \
python2.7-minimal python3 python3-apt python3-dbus python3-gi \
python3-minimal python3-software-properties python3.6 python3.6-minimal \
readline-common sed sensible-utils shared-mime-info \
software-properties-common sudo sysvinit-utils tar tcl tcl8.6 tzdata \
ubuntu-keyring ucf udev udhcpc unattended-upgrades util-linux whiptail \
xdg-user-dirs xz-utils zlib1g \
"
