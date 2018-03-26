SUMMARY = "fsl monitor pkgs"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS_packagegroup-fsl-monitor = "\
    coreutils \
    cronie \
    bc \
    lighttpd \
    lighttpd-module-cgi \
    lmsensors-sensors \
    cairo \
    cairo-dev \
    rrdtool \
    liberation-fonts \
    make \
"

RDEPENDS_packagegroup-fsl-monitor_remove_ls102xa = "\
    web-sysmon \
"
