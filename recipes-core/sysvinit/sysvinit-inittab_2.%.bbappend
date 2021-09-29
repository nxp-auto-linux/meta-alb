# Copyright 2021 NXP
# Comment lines in inittab with getty spawn on default serial consoles if Xen Dom0less enabled

INITTAB_DOM0LESS_FILE = "sysvinit-inittab_xen-nxp.inc"
INITTAB_DOM0LESS = " \
    ${@bb.utils.contains('XEN_EXAMPLE', 'xen-examples-dom0less', '${INITTAB_DOM0LESS_FILE}', \
       bb.utils.contains('XEN_EXAMPLE', 'xen-examples-dom0less-passthrough', '${INITTAB_DOM0LESS_FILE}', \
       bb.utils.contains('XEN_EXAMPLE', 'xen-examples-dom0less-passthrough-gmac', '${INITTAB_DOM0LESS_FILE}', '', d), d), d)} \
"
require ${INITTAB_DOM0LESS}
