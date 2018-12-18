
# For aarch64 platforms with 32bit bus we don't want STRICT alignment, 
# since this would align every access to 64bit.
CFLAGS_remove_s32v2xx = "-DFORCE_STRICT_ALIGNMENT"
