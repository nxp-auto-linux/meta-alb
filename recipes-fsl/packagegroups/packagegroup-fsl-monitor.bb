SUMMARY = "fsl monitor pkgs"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:packagegroup-fsl-monitor = "\
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

RDEPENDS:packagegroup-fsl-monitor:remove:ls102xa = "\
    web-sysmon \
"
