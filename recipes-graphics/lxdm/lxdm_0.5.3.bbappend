FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# The Xlxdm wrapper is used to filter arguments passed from lxdm
# to the Xserver. Without it, use of, e.g., Xvfb wouldn't work.
SRC_URI += "\
    file://Xlxdm \
"

do_install:append() {
    install ${WORKDIR}/Xlxdm ${D}${sysconfdir}/lxdm


    # We modify the configuration to use Xlxdm. This could be done with a patch, too.
    sed -i "s:^arg=.*$:arg=${sysconfdir}/lxdm/Xlxdm:g" ${D}${sysconfdir}/lxdm/lxdm.conf
}

pkg_postinst:${PN}:append() {
    # We don't know at recipe build time what kind of session options the user has.
    # So we check at startup time and fix the lxdm config appropriately
    if [ -f /usr/bin/startxfce4 ]; then
        sed -i "s:^session=.*$:session=/usr/bin/startxfce4 --with-ck-launch:g" $D${sysconfdir}/lxdm/lxdm.conf
    fi
}


