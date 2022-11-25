DEPENDS:append:qoriq = " patchelf-native"

RPROVIDES:${PN}:qoriq += "imx-gpu-viv"
EXTRA_PROVIDES:append:qoriq = " \
    virtual/libgl \
    virtual/libgles1 \
    virtual/libgles2 \
"
EXTRA_PROVIDES:append:qoriq = " \
    virtual/libgbm \
"
INSANE_SKIP:libvdk-imx += "dev-deps"
INSANE_SKIP:libegl-imx += "dev-deps"

IMX_PACKAGES_GBM:qoriq = "libgbm-imx libgbm-imx-dev"
HAS_GBM:qoriq = "true"
IS_MX8:qoriq = "1"
FILES:libgbm-imx:qoriq = "${libdir}/libgbm*${SOLIBS}"

RDEPENDS:libgal-imx:remove = "kernel-module-imx-gpu-viv"
COMPATIBLE_MACHINE = "(imxfbdev|imxgpu|qoriq)"
