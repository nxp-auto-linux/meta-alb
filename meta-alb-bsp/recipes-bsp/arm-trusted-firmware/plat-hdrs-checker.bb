# Copyright 2025 NXP

DESCRIPTION = "Checker for header file differences between TF-A and Linux"
LICENSE = "CLOSED"

DEPENDS:append:s32cc = " linux-s32-plat-hdrs arm-trusted-firmware-plat-hdrs"

do_configure() {
    LINUX_HDRS="${RECIPE_SYSROOT}${includedir}/plat-hdrs/linux"
    ATF_HDRS="${RECIPE_SYSROOT}${includedir}/plat-hdrs/tf-a"

    for hdr in ${LINUX_HDRS}/*; do
        bname="$(basename ${hdr})"
        atf_hdr=$(find "${ATF_HDRS}" -name "${bname}")

        if [ -z "${atf_hdr}" ]; then
            bbfatal_log "Failed to locate ${bname} header file"
        fi

        if ! diff -NZbau "${hdr}" "${atf_hdr}"; then
            bbfatal_log "There is a difference in the platform header content between TF-A and the Linux repository (${hdr} vs ${lhdr})."
        fi
    done
}

COMPATIBLE_MACHINE = "s32cc"
