# Turns out that if packages are added via a packagegroup and not directly,
# you have to override the packagegroup because you IMAGE_INSTALL does not
# contain the package name that you may want to remove.
RRECOMMENDS_packagegroup-fsl-extend-misc_remove = "nginx"

# LS2 EAR5 is a bit backwards compared to SDK V1.7. We change a few
# things again to be more full featured.
RDEPENDS_packagegroup-fsl-extend-misc += "\
    chrpath \
    git-perltools \
    texinfo \
"

# The LS2 EAR4 default python is 2.7.3, which isn't quite complete for
# aarch64. We switch the packages around to get python 3.
#RDEPENDS_packagegroup-fsl-extend-python = "\
#    python3 \
#    python3-misc \
#    python3-modules \
#"