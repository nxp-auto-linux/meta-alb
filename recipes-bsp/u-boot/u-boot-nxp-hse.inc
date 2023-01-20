HSE_CONFIG = "hse_config.cfg"

DELTA_UBOOT_DEFCONFIG:append:s32cc = " ${HSE_CONFIG}"
SRC_URI:append:s32cc = " \
    file://build/${HSE_CONFIG} \
"

DEPENDS:append = " hse-firmware"

do_add_hse_fw_path[dirs] = "${B}"
do_add_hse_fw_path() {
	
	hse_fw=\""${STAGING_BASELIBDIR}/firmware/s32cc_hse_fw.bin\""
	sed -i "s,\(CONFIG_NXP_HSE_FW_FILE=\).*,\1${hse_fw}," ${HSE_CONFIG}
}

addtask add_hse_fw_path before do_merge_delta_config after do_patch