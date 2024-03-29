SUMMARY = "A system tool for maintaining the /etc/rc*.d hierarchy"
DESCRIPTION = "Chkconfig is a basic system utility.  It updates and queries runlevel \
information for system services.  Chkconfig manipulates the numerous \
symbolic links in /etc/rc.d, to relieve system administrators of some \
of the drudgery of manually editing the symbolic links."

RECIPE_NO_UPDATE_REASON = "Version 1.5 requires selinux"

HOMEPAGE = "https://github.com/fedora-sysv"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=5574c6965ae5f583e55880e397fbb018"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

DEPENDS = "libnewt popt"
PROVIDES += "virtual/update-alternatives"

PR = "r7"

S = "${WORKDIR}/${BPN}-${BPN}-${PV}"

SRC_URI = "git://github.com/fedora-sysv/chkconfig.git \
           file://replace_caddr_t.patch \
          "

SRCREV = "3e0b3909ff6328d232b27b9c32f1a9309c6cd0a4"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit gettext

# Makefile uses RPM_OPT_FLAGS to construct CFLAGS
#
EXTRA_OEMAKE = "\
    'RPM_OPT_FLAGS=${CFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
    'BINDIR=${base_sbindir}' \
    'SBINDIR=${sbindir}' \
    'MANDIR=${mandir}' \
    'ALTDIR=${localstatedir}/lib/alternatives' \
    'ALTDATADIR=${sysconfdir}/alternatives' \
"

do_unpack[postfuncs] += "obey_variables"
do_unpack[vardeps] += "obey_variables"
obey_variables () {
	sed -i -e 's,/etc,${sysconfdir},; s,/lib/systemd,${base_libdir}/systemd,' ${S}/leveldb.h
	sed -i -e 's,/etc/alternatives,${sysconfdir}/alternatives,' \
	       -e 's,/var/lib/alternatives,${localstatedir}/lib/alternatives,' \
	       -e 's,/usr/share/locale,${datadir}/locale,' ${S}/alternatives.c
}

do_install() {
	oe_runmake 'DESTDIR=${D}' 'INSTALLNLSDIR=${D}${datadir}/locale' \
		'BINDIR=${sbindir}' install
	install -d ${D}${sysconfdir}/chkconfig.d
}

PACKAGES =+ "${PN}-alternatives ${PN}-alternatives-doc"
SUMMARY:${PN}-alternatives = "Maintain symbolic links determining default commands"
DESCRIPTION:${PN}-alternatives = "alternatives creates, removes, maintains and displays \
information about the symbolic links comprising the alternatives system."
SUMMARY:${PN}-alternatives-doc = "${SUMMARY_${PN}-alternatives} - Documentation files"
DESCRIPTION:${PN}-alternatives-doc = "${DESCRIPTION_${PN}-alternatives}  \
This package contains documentation."
RPROVIDES:${PN}-alternatives += "update-alternatives"
RCONFLICTS:${PN}-alternatives = "update-alternatives-opkg update-alternatives-dpkg"
FILES:${PN}-alternatives = "${sbindir}/alternatives ${sbindir}/update-alternatives \
			    ${sysconfdir}/alternatives ${localstatedir}/lib/alternatives"
FILES:${PN}-alternatives-doc = "${mandir}/man8/alternatives.8 \
                                ${mandir}/man8/update-alternatives.8"
