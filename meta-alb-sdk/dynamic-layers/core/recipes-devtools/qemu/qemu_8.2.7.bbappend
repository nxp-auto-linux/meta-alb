FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

include ${@'qemu-patches.inc' if 'ubuntu' in (d.getVar('MACHINEOVERRIDES') or '') else ''}

DEPENDS:append:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' xen-tools', '', d)}"
