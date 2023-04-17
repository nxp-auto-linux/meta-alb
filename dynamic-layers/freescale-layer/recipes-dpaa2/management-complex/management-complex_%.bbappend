# Missing part family compatibility specifications
REGLEX:ls2084a = "ls2088a"
REGLEX:ls2044a = "ls2088a"
REGLEX:ls2048a = "ls2088a"
REGLEX:lx2120a = "lx2160a"
REGLEX:lx2080a = "lx2160a"

do_deploy () {
    install -d ${DEPLOYDIR}/mc_app
    rm -f ${DEPLOYDIR}/mc_app/*.itb
    install -m 755 ${S}/${REGLEX}/*.itb ${DEPLOYDIR}/mc_app
    # make a symlink to the latest binary
    for mc_binary in `ls ${DEPLOYDIR}/mc_app |sort`;do
        ln -sfT ${mc_binary} ${DEPLOYDIR}/mc_app/mc.itb
    done
}
