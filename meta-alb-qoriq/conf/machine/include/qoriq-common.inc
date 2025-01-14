# This file serves for common QorIQ machine settings/workarounds

# meta-qoriq has a single dangling bbappend for an image that we are not
# providing. Hence, we mask this bbappend file.
BBMASK += "meta-qoriq/recipes-fsl/images/fsl-image-multimedia-full.bbappend"

# To avoid ambiguity across SoC families we need to precisely specify
# some versions. It would be nice to always take the latest and greatest,
# but it doesn't quite work that way.
PREFERRED_VERSION_linux-qoriq ?= "5.15%"
PREFERRED_VERSION_linux-flash ?= "${PREFERRED_VERSION_linux-qoriq}"

# We don't want the QorIQ Ethernet naming rewrites
# This affects all images not only based on core-image-minimal
MACHINE_EXTRA_RRECOMMENDS:remove = "udev-rules-qoriq"

# Artifact types for QorIQ machines
IMAGE_CLASSES += "\
	image_types_fsl_flashimage \
	image_types_fsl_sdcard \
	image_types_fsl_itb \
"
