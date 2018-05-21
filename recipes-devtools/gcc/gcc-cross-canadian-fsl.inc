DEPENDS_append = " nativesdk-isl"
RDEPENDS_${PN}_append = " nativesdk-isl"

EXTRA_OECONF_append = " --with-isl=${STAGING_DIR_HOST}${SDKPATHNATIVE}${prefix_nativesdk} \
"
