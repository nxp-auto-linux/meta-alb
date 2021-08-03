# Copyright 2020 NXP
# Comment lines in inittab with getty spawn on default serial consoles if Xen enabled

require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'sysvinit-inittab_xen-nxp.inc', '', d)}
