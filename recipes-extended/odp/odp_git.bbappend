FILESEXTRAPATHS:prepend := "${THISDIR}/odp:"
SRC_URI += "file://0001-libio-In-the-latest-glib-headers-libio.h-has-been-de.patch"

# Disable GCC 9 warning about pointers to members of a packed struct
TARGET_CFLAGS += "-Wno-address-of-packed-member -Wno-array-bounds"

# Add -fcommon to CFLAGS to silence "multiple definition" errors
# due to gcc 10 setting -fno-common by default
TARGET_CFLAGS += "-fcommon"

COMPATIBLE_MACHINE:append = "|(ls2084a)"
