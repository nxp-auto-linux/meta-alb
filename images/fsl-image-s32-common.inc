#
# Copyright 2017,2018 NXP
#

# Support for SJA1105 swich under Linux
IMAGE_INSTALL_append_s32 = " sja1105 "

# Enable LXC features
IMAGE_INSTALL_append_s32 = "${@bb.utils.contains('DISTRO_FEATURES', 'lxc', ' lxc', '', d)}"

