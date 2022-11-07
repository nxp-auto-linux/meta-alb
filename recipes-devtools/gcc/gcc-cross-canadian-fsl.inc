DEPENDS:append = " nativesdk-isl"
RDEPENDS:${PN}:append = " nativesdk-isl"

EXTRA_OECONF:append = " --with-isl=${STAGING_DIR_HOST}${SDKPATHNATIVE}${prefix_nativesdk} \
"
