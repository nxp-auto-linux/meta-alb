# Only override poky's qemu patch dir for ubuntu machines. Our qemu/ dir ships a
# rebased 0011-...MAP_FIXED_NOREPLACE.patch that applies on top of our 0001
# (do_libcsyscall) patch; on non-ubuntu builds 0001 is not applied, so leaking our
# copy here makes poky's 0011 apply with fuzz and fail the patch-fuzz QA check.
FILESEXTRAPATHS:prepend := "${@'${THISDIR}/${BPN}:' if 'ubuntu' in (d.getVar('MACHINEOVERRIDES') or '') else ''}"

include ${@'qemu-patches.inc' if 'ubuntu' in (d.getVar('MACHINEOVERRIDES') or '') else ''}
