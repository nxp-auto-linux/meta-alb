# This file aims to solve backwards compatibility issues
# related to the sja module using different repos for
# different kernel versions.
# Although the default kernel version is handled by
# the main recipe, this file overrides the default
# settings for the kernel version it serves.

# This is for kernel 4.1.
SRC_URI = "git://source.codeaurora.org/external/autobsps32/sja1105x;branch=alb-4.1;protocol=https"
SRCREV = "f0c783abe9e6aad16f2188bff941e6a0e237cd56"
