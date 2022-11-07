FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# Prevent splitting busybox applets in two binaries (*.suid and *.nosuid)
BUSYBOX_SPLIT_SUID = "0"
ALTERNATIVE_PRIORITY[init] = "40"

SRC_URI += " \
    file://externaldhcp.cfg \
    file://init.cfg \
    file://enabledhcpcopts.patch \
"

# disable FTPD - as it's provided by inetutils
do_configure:append() {
    sed -i "/CONFIG_FTPD/c\# CONFIG_FTPD is not set" .config
}

do_install:append () {

    if [ "${@(d.getVar('VIRTUAL-RUNTIME_init_manager'))}" != "busybox" ]; then
        rm -f ${D}${sysconfdir}/init.d/rcS
        rm -f ${D}${sysconfdir}/init.d/rcK
        rm -f ${D}${sysconfdir}/inittab
        rm -f ${D}${base_sbindir}/runlevel
        # remove sbin if empty
        rm -df ${D}${base_sbindir}
    fi
}
