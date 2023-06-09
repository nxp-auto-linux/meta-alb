# LLCE support

SRC_URI += "\
    file://build/llce_can.config \
    file://build/llce_logger.config \
    file://scripts/get_node_path.py \
"

DEPENDS:append = " python-fdt-native"

LLCE_FEATURES := "${@bb.utils.contains_any('DISTRO_FEATURES', 'llce-can llce-multihost-primary llce-multihost-secondary', ' can', ' no-can', d)}"
LLCE_FEATURES:append := "${@bb.utils.contains('DISTRO_FEATURES', 'llce-logger', ' logger', '', d)}"
LLCE_FEATURES:append := "${@bb.utils.contains('DISTRO_FEATURES', 'llce-multihost-secondary', ' nofwload', '', d)}"
LLCE_FEATURES:append := "${@bb.utils.contains('LLCE_FEATURES', 'no-can', ' nofwload', '', d)}"

# LLCE config fragments
DELTA_KERNEL_DEFCONFIG:append:s32g := "${@bb.utils.contains('LLCE_FEATURES', 'can', ' llce_can.config', '', d)}"
DELTA_KERNEL_DEFCONFIG:append:s32g := "${@bb.utils.contains('LLCE_FEATURES', 'logger', ' llce_logger.config', '', d)}"

# Core
KERNEL_MODULE_AUTOLOAD:append:s32g = "${@bb.utils.contains_any('LLCE_FEATURES', 'can logger', ' llce-core', '', d)}"
KERNEL_MODULE_PROBECONF += "${@bb.utils.contains('LLCE_FEATURES', 'nofwload', 'llce-core', '', d)}"
module_conf_llce-core = "${@bb.utils.contains('LLCE_FEATURES', 'nofwload', 'options llce-core load_fw=0', '', d)}"

# Mailbox
KERNEL_MODULE_AUTOLOAD:append:s32g = "${@bb.utils.contains_any('LLCE_FEATURES', 'can logger', ' llce-mailbox', '', d)}"
KERNEL_MODULE_PROBECONF += "${@bb.utils.contains('LLCE_FEATURES', 'no-can', 'llce-mailbox', '', d)}"
module_conf_llce-mailbox = "${@bb.utils.contains('LLCE_FEATURES', 'no-can', 'options llce-mailbox config_platform=0', '', d)}"

# CAN
KERNEL_MODULE_AUTOLOAD:append:s32g = "${@bb.utils.contains('LLCE_FEATURES', 'can', ' llce_can', '', d)}"

# Logger
KERNEL_MODULE_AUTOLOAD:append:s32g = "${@bb.utils.contains('LLCE_FEATURES', 'logger', ' llce_logger', '', d)}"

RDEPENDS:kernel-base:append = "${@bb.utils.contains('LLCE_FEATURES', 'can', ' iproute2-devlink', '', d)}"
RDEPENDS:kernel-base:append = "${@bb.utils.contains('LLCE_FEATURES', 'nofwload', '', ' linux-firmware-llce-can', d)}"

python () {
    mh_primary = bb.utils.contains('DISTRO_FEATURES', 'llce-multihost-primary', True, False, d)
    mh_secondary = bb.utils.contains('DISTRO_FEATURES', 'llce-multihost-secondary', True, False, d)
    not_mh = bb.utils.contains('DISTRO_FEATURES', 'llce-can', True, False, d)

    if mh_primary and mh_secondary:
        bb.fatal("Invalid configuration. Please select llce-multihost-primary or llce-multihost-secondary.")

    if not_mh and (mh_primary or mh_secondary):
        bb.fatal("Invalid configuration. The llce-can feature is intended for non-multihost scenarios and cannot be used in conjunction with llce-multihost-primary or llce-multihost-secondary features.")
}

do_compile:append() {
    if ${@bb.utils.contains_any('DISTRO_FEATURES', 'llce-multihost-primary llce-multihost-secondary', 'true', 'false', d)}; then
        echo "Adding LLCE multihif property"
        mailbox_path="$(nativepython3 "${WORKDIR}/scripts/get_node_path.py" "${dtb_name}" --compatible 'nxp,s32g-llce-mailbox')"
        fdtput "${dtb_name}" "${mailbox_path}" "nxp,multi-hif"
    fi
}
