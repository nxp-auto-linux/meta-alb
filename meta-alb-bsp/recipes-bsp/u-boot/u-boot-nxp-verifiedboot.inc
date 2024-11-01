VERIFIEDBOOT_CONFIG = "verifiedboot_config.cfg"

DELTA_UBOOT_DEFCONFIG:append:s32cc = " ${VERIFIEDBOOT_CONFIG}"
SRC_URI:append:s32cc = " \
    file://build/${VERIFIEDBOOT_CONFIG} \
"

do_merge_delta_config:append:s32cc () {
       for config in $(echo ${UBOOT_MACHINE} | sed "s/ /\n/g" | grep -v '_qspi_defconfig\>' || true ); do
              echo "CONFIG_BOOTARGS=\"root=/dev/mmcblk0p3 rw earlycon loglevel=7\"" >> ${S}/configs/${config}
       done
}