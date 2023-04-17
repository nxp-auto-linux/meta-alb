FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "\
    file://0001-dpdk-extras-Added-support-for-LS2084A.patch \
"

# Add -fcommon to CFLAGS to silence "multiple definition" errors
# due to gcc 10 setting -fno-common by default
TOOLCHAIN_OPTIONS += "-fcommon"

COMPATIBLE_MACHINE:append = "|(ls2084a)"
