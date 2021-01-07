DEPENDS_append_qoriq = " patchelf-native"

RPROVIDES_${PN}_qoriq += "imx-gpu-viv"
EXTRA_PROVIDES_append_qoriq = " \
    virtual/libgl \
    virtual/libgles1 \
    virtual/libgles2 \
"
EXTRA_PROVIDES_append_qoriq = " \
    virtual/libgbm \
"
INSANE_SKIP_libvdk-imx += "dev-deps"
INSANE_SKIP_libegl-imx += "dev-deps"

IMX_PACKAGES_GBM_qoriq = "libgbm-imx libgbm-imx-dev"
HAS_GBM_qoriq = "true"
IS_MX8_qoriq = "1"
FILES_libgbm-imx_qoriq = "${libdir}/libgbm*${SOLIBS}"

RDEPENDS_libgal-imx_remove = "kernel-module-imx-gpu-viv"
COMPATIBLE_MACHINE = "(imxfbdev|imxgpu|qoriq)"
