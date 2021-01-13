
# Revert RTE_SDK from meta-qoriq as it breaks the build
export RTE_SDK = "${RECIPE_SYSROOT}/usr/share/"
EXTRA_OEMAKE += 'RTE_SDK="${RECIPE_SYSROOT}/usr/share/"'

# Add -fcommon to CFLAGS to silence "multiple definition" errors
# due to gcc 10 setting -fno-common by default
TOOLCHAIN_OPTIONS += "-fcommon"
