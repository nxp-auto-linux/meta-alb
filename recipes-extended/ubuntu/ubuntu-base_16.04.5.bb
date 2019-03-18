SUMMARY = "A prebuilt Ubuntu Base image as baseline for custom work"
require ubuntu-license.inc
SECTION = "devel"

# Ubuntu 16.04.5
SRC_URI[md5sum] = "f8013a313d868ed334c17682e2651b32"
SRC_URI[sha256sum] = "aa9771e13631b1b65308027ce5e8d7aa86191e8d38a290d3b8319355fe5093e7"

require ubuntu-base.inc
require ubuntu-tarfiles.inc

# There are some basic differences between different Ubuntu versions.
# We try not to address them in the generic recipe
APTGET_EXTRA_PACKAGES += "resolvconf"

# We should not have a single PROVIDES entry as this package
# does not provide anything for build time of any other package!
# PROVIDES += ""

# This is the installed package list as found in log_do_install.
# Minor edits have been done to remove an architecture suffix.
APTGET_RPROVIDES += " \
adduser apt apt-utils base-files base-passwd bash bc bsdutils busybox \
coreutils dash db5.3-doc db5.3-sql-util db5.3-util debconf debianutils \
diffutils dpkg e2fslibs e2fsprogs file findutils gcc-5-base gcc-6-base \
gcj-5-jre-lib gnupg gpgv grep gzip hostname htop ifupdown init \
init-system-helpers initscripts insserv iproute2 isc-dhcp-client \
isc-dhcp-common kmod libacl1 libapparmor1 libapt-inst2.0 libapt-pkg5.0 \
libasound2 libasound2-data libatm1 libattr1 libaudit-common libaudit1 \
libblkid1 libbz2-1.0 libc-bin libc6 libcap2 libcap2-bin libcomerr2 \
libcryptsetup4 libdb5.3 libdb5.3++ libdb5.3++-dev libdb5.3-dbg \
libdb5.3-dev libdb5.3-java libdb5.3-java-dev libdb5.3-java-gcj \
libdb5.3-java-jni libdb5.3-sql libdb5.3-sql-dev libdb5.3-stl \
libdb5.3-stl-dev libdb5.3-tcl libdebconfclient0 libdevmapper1.02.1 \
libdns-export162 libexpat1 libfdisk1 libffi6 libfribidi0 libgcc1 \
libgcj-bc libgcj-common libgcj16 libgcrypt20 libgmp10 libgpg-error0 \
libisc-export160 libkmod2 liblz4-1 liblzma5 libmagic1 libmnl0 libmount1 \
libmpdec2 libncurses5 libncursesw5 libnewt0.52 libpam-modules \
libpam-modules-bin libpam-runtime libpam0g libpcre3 libpng12-0 libpopt0 \
libprocps4 libpython-stdlib libpython2.7-minimal libpython2.7-stdlib \
libpython3.5-minimal libpython3.5-stdlib libreadline6 libseccomp2 \
libselinux1 libsemanage-common libsemanage1 libsepol1 libslang2 \
libsmartcols1 libsqlite3-0 libss2 libssl1.0.0 libstdc++6 libsystemd0 \
libtcl8.6 libtinfo5 libudev1 libusb-0.1-4 libustr-1.0-1 libuuid1 \
libxtables11 login lsb-base makedev mawk mime-support mount \
multiarch-support ncurses-base ncurses-bin net-tools netbase passwd \
perl-base procps python python-minimal python2.7 python2.7-minimal \
python3.5 python3.5-minimal readline-common sed sensible-utils sudo \
systemd systemd-sysv sysv-rc sysvinit-utils tar tcl tcl8.6 tzdata \
ubuntu-keyring udev udhcpc util-linux whiptail xz-utils zlib1g \
"

