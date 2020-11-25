FILESEXTRAPATHS_prepend := "${THISDIR}/odp:"
SRC_URI += "file://0001-libio-In-the-latest-glib-headers-libio.h-has-been-de.patch"

# Disable GCC 9 warning about pointers to members of a packed struct
TARGET_CFLAGS += "-Wno-address-of-packed-member -Wno-array-bounds"

COMPATIBLE_MACHINE_append = "|(ls2084a)"
