
# For aarch64 platforms with 32bit bus we don't want STRICT alignment, 
# since this would align every access to 64bit.
CFLAGS_remove_s32v2xx = "-DFORCE_STRICT_ALIGNMENT"


FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
SRC_URI_append_s32v2xx = " file://0001-Force-32bit-access-for-type-w-type-and-add-type-d-fo.patch"
