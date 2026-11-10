# Quick boot kernel configuration fragments.
#
# QB_KERNEL_FEATURES lists the fragments that a SoC family provides. An entry
# is either <feature>, for the fragment quickboot-<feature>.cfg, or
# <feature>:<soc>, for the fragment quickboot-<feature>-<soc>.cfg. A feature
# can therefore have a common fragment and one fragment for each SoC family.
# Every entry is listed explicitly, so a SoC family picks the ones it needs.
#
# The feature 'generic' is used when the 'quick-boot' DISTRO_FEATURE is set.
# Any other entry is used when its feature is also set in QUICK_BOOT_CONFIG.
# The fragments are applied in the order of the list, so <feature>:<soc> can
# override <feature> when it is listed after it. A fragment that does not exist
# stops the build in do_fetch.
#
# The fragments exist only for the kernel versions in QB_KERNEL_VERSIONS. For
# another version quick boot does not change the kernel configuration.
QB_KERNEL_VERSIONS ?= "6.12"

# The HSE RNG fragment needs HSE and does not apply with secure boot.
QB_KERNEL_HSERNG:s32cc = "${@'hserng' if 'hse' in d.getVar('DISTRO_FEATURES').split() and 'secboot' not in d.getVar('DISTRO_FEATURES').split() else ''}"

QB_KERNEL_FEATURES ?= ""
QB_KERNEL_FEATURES:append:s32cc = " generic skippcie:s32cc kernelextmodules systemd-debug ${QB_KERNEL_HSERNG}"

QB_KERNEL_ENABLED = "${@'generic ' + d.getVar('QB_CONFIG') if d.getVar('PV_MAJ_VER') in d.getVar('QB_KERNEL_VERSIONS').split() else ''}"
QB_KERNEL_FRAGMENTS = "${@' '.join('quickboot-%s.cfg' % f.replace(':', '-') for f in d.getVar('QB_KERNEL_FEATURES').split() if f.split(':')[0] in d.getVar('QB_KERNEL_ENABLED').split())}"

SRC_URI:append = "${@''.join(' file://' + f for f in d.getVar('QB_KERNEL_FRAGMENTS').split())}"
DELTA_KERNEL_DEFCONFIG:append = " ${QB_KERNEL_FRAGMENTS}"
