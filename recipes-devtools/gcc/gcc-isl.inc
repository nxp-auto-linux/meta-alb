

# Configure gcc to use isl
EXTRA_OECONF_BASE_remove = " --without-isl"
NATIVEDEPS += " isl-native"
