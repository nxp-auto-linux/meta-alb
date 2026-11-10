# Quick boot U-Boot configuration fragments.
#
# QB_UBOOT_FEATURES lists the fragments that a SoC family provides. An entry is
# either <feature>, for the fragment quickboot_<feature>.cfg, or
# <feature>:<soc>, for the fragment quickboot_<feature>-<soc>.cfg. A feature
# can therefore have a common fragment and one fragment for each SoC family.
# Every entry is listed explicitly, so a SoC family picks the ones it needs.
#
# The feature 'generic' is used when the 'quick-boot' DISTRO_FEATURE is set.
# Any other entry is used when its feature is also set in QUICK_BOOT_CONFIG.
# The fragments are applied in the order of the list, so <feature>:<soc> can
# override <feature> when it is listed after it. A fragment that does not exist
# stops the build in do_fetch.
QB_UBOOT_FEATURES ?= ""
QB_UBOOT_FEATURES:append:s32cc = " generic generic:s32cc nokernellog noubootlog skipubootnet skippcie"

QB_UBOOT_ENABLED = "generic ${QB_CONFIG}"
QB_UBOOT_FRAGMENTS = "${@' '.join('quickboot_%s.cfg' % f.replace(':', '-') for f in d.getVar('QB_UBOOT_FEATURES').split() if f.split(':')[0] in d.getVar('QB_UBOOT_ENABLED').split())}"

SRC_URI:append = "${@''.join(' file://build/' + f for f in d.getVar('QB_UBOOT_FRAGMENTS').split())}"
DELTA_UBOOT_DEFCONFIG:append = " ${QB_UBOOT_FRAGMENTS}"
