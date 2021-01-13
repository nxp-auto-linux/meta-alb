
# Add -fcommon to CFLAGS to silence "multiple definition" errors
# due to gcc 10 setting -fno-common by default
TOOLCHAIN_OPTIONS += "-fcommon"
