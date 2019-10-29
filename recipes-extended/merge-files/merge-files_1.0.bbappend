#
# We want to merge files, but as opposed to the original recipe,
# we don't want to rebuild and merge every single time as we
# assume that our files to merge are semi-static for a build.
# If someone wants to change the files ot be merged, then the
# recipe needs to be cleansstate'd to be unpacked again.
#
do_install () {
    install -d ${D}/${MERGED_DST}
    find ${WORKDIR}/merge/ -maxdepth 1 -mindepth 1 -not -name README \
    -exec cp -fr '{}' ${D}/${MERGED_DST}/ \;
}
python __anonymous () {
    d.delVarFlag('do_unpack', 'nostamp')
    d.delVarFlag('do_install', 'nostamp')
}
