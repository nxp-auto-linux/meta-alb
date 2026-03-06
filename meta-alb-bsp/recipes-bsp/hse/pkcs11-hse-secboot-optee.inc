# Copyright 2026 NXP
#
# OP-TEE specific build and install of hse-secboot executables

do_compile:append() {
        # Save the hse-secboot executable before cleaning
        install -D -m 0755 ${S}/examples/hse-secboot/hse-secboot ${B}/hse-secboot-default

        # Rebuild the hse-secboot with optee_ks_config.h enabling KS_CONFIG_OPTEE_EN flag
        oe_runmake -C ${S}/examples/hse-secboot clean
        oe_runmake -C ${S}/examples/hse-secboot KS_CONFIG_OPTEE_EN=true

        # Save the OP-TEE executable under optee-hse-secboot name
        install -D -m 0755 ${S}/examples/hse-secboot/hse-secboot ${B}/optee-hse-secboot
}

do_install:append() {
        # Overwrite /usr/bin/hse-secboot with the saved default build
        install -m 0755 ${B}/hse-secboot-default ${D}${bindir}/hse-secboot

        # Install the OP-TEE executable alongside the default one in /usr/bin
        install -m 0755 ${B}/optee-hse-secboot ${D}${bindir}/
}
