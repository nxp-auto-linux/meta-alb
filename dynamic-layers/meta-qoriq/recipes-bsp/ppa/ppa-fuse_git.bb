require recipes-bsp/ppa/ppa.inc

DEPENDS += "cst-native"

PPA_PATH_ls1046a = "ls1046"

do_compile() {
    export ARMV8_TOOLS_DIR="${STAGING_BINDIR_TOOLCHAIN}"
    export ARMV8_TOOLS_PREFIX="${TARGET_PREFIX}"
    export CROSS_COMPILE="${WRAP_TARGET_PREFIX}"
    cd ${RECIPE_SYSROOT_NATIVE}/usr/bin/cst
    ./gen_fusescr input_files/gen_fusescr/ls104x_1012/input_fuse_file
    cp fuse_scr.bin ${S}/ppa/soc-${PPA_PATH}/fuse_scr.bin
    cd ${S}/ppa
    ./build rdb-fit fuse=on ${PPA_PATH}
    cd ${S}
}

PARALLEL_MAKE = ""
COMPATIBLE_MACHINE = "(ls1046a)"
