# LLCE support

SRC_URI += "\
    file://build/llce_can.config \
    file://build/llce_logger.config \
    file://build/llce_base.config \
    file://scripts/get_node_path.py \
"

DEPENDS:append = " python-fdt-native"

LLCE_FEATURES := "${@bb.utils.contains_any('DISTRO_FEATURES', 'llce-can llce-can-multihost', ' can', ' no-can', d)}"
LLCE_FEATURES:append := "${@bb.utils.contains('DISTRO_FEATURES', 'llce-can-logger', ' logger', '', d)}"
LLCE_FEATURES:append := "${@bb.utils.contains('DISTRO_FEATURES', 'llce-fw-load', '', ' nofwload', d)}"
LLCE_FEATURES:append := "${@bb.utils.contains('DISTRO_FEATURES', 'llce-linflex', ' lin', '', d)}"
LLCE_FEATURES:append := "${@bb.utils.contains('DISTRO_FEATURES', 'llce-lpspi', ' lpspi', '', d)}"

# LLCE config fragments
DELTA_KERNEL_DEFCONFIG:append:s32g := "${@bb.utils.contains('LLCE_FEATURES', 'can', ' llce_can.config', '', d)}"
DELTA_KERNEL_DEFCONFIG:append:s32g := "${@bb.utils.contains('LLCE_FEATURES', 'logger', ' llce_logger.config', '', d)}"
DELTA_KERNEL_DEFCONFIG:append:s32g := "${@bb.utils.contains_any('LLCE_FEATURES', 'lin lpspi', ' llce_base.config', '', d)}"

# Core
KERNEL_MODULE_AUTOLOAD:append:s32g = "${@bb.utils.contains_any('LLCE_FEATURES', 'can logger lin', ' llce-core', '', d)}"
KERNEL_MODULE_PROBECONF += "${@bb.utils.contains('LLCE_FEATURES', 'nofwload', 'llce-core', '', d)}"
module_conf_llce-core = "${@bb.utils.contains('LLCE_FEATURES', 'nofwload', 'options llce-core load_fw=0', '', d)}"

# Mailbox
KERNEL_MODULE_AUTOLOAD:append:s32g = "${@bb.utils.contains_any('LLCE_FEATURES', 'can logger lin', ' llce-mailbox', '', d)}"
KERNEL_MODULE_PROBECONF += "${@bb.utils.contains('LLCE_FEATURES', 'no-can', 'llce-mailbox', '', d)}"
module_conf_llce-mailbox = "${@bb.utils.contains('LLCE_FEATURES', 'no-can', 'options llce-mailbox config_platform=0', '', d)}"

# CAN
KERNEL_MODULE_AUTOLOAD:append:s32g = "${@bb.utils.contains('LLCE_FEATURES', 'can', ' llce_can', '', d)}"

# Logger
KERNEL_MODULE_AUTOLOAD:append:s32g = "${@bb.utils.contains('LLCE_FEATURES', 'logger', ' llce_logger', '', d)}"

RDEPENDS:kernel-base:append = "${@bb.utils.contains('LLCE_FEATURES', 'can', ' iproute2-devlink', '', d)}"
RDEPENDS:kernel-base:append = "${@bb.utils.contains('LLCE_FEATURES', 'nofwload', '', ' linux-firmware-llce', d)}"

python () {
    mh = bb.utils.contains('DISTRO_FEATURES', 'llce-can-multihost', True, False, d)
    not_mh = bb.utils.contains('DISTRO_FEATURES', 'llce-can', True, False, d)

    if not_mh and mh:
        bb.fatal("Invalid configuration. The llce-can feature is intended for non-multihost scenarios and cannot be used in conjunction with llce-can-multihost feature.")
}

do_compile:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'llce-can-multihost', 'true', 'false', d)}; then
        echo "Adding LLCE multihif property"
        mailbox_path="$(nativepython3 "${WORKDIR}/scripts/get_node_path.py" "${dtb_name}" --compatible 'nxp,s32g-llce-mailbox')"
        fdtput "${dtb_name}" "${mailbox_path}" "nxp,multi-hif"
    fi
}
