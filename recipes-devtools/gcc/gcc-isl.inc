

# Configure gcc to use isl
EXTRA_OECONF_BASE:remove = " --without-isl"
NATIVEDEPS += " isl-native"
