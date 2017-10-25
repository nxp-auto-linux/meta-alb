# LS2 EAR5 is a bit backwards compared to SDK V1.7. We change a few
# things again to be more full featured.
RDEPENDS_packagegroup-fsl-core-misc += "\
    inetutils-tftp \
    inetutils-tftpd \
"

RDEPENDS_packagegroup-fsl-core-misc_remove += "\
    merge-files \
"