# Ensure that the versioned recipe is always preferred over this one.
# In order to use this recipe, add the following to either local.conf
# or an image recipe.
#
#  PREFERRED_VERSION_opendds="1.0+git%"
#
# It is not guaranteed to build, especially since there may be bbappend
# files in layers not up to date.

DEFAULT_PREFERENCE = "-1"

# Checkout the head of the master branch
SRCREV = "${AUTOREV}"
PV = "1.0+git${SRCPV}"

require opendds.inc
