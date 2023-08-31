# This file serves for common Qoriq machine settings/workarounds

# meta-qoriq has a single dangling bbappend for an image that we are not
# providing. Hence, we mask this bbappend file.
BBMASK += "meta-qoriq/recipes-fsl/images/fsl-image-multimedia-full.bbappend"
