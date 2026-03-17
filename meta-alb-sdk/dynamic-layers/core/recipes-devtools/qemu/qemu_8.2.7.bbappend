FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

include ${@'qemu-patches.inc' if 'ubuntu' in (d.getVar('MACHINEOVERRIDES') or '') else ''}
