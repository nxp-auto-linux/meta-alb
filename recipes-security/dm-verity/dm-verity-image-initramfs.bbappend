#
# Copyright 2023 NXP
#

STAGING_VERITY_DIR ?= "${TMPDIR}/work-shared/${MACHINE}/dm-verity"
DM_VERITY_PUBLIC_KEY ?= "${DEPLOY_DIR_IMAGE}/verity_public.pem"
DM_VERITY_ROOT_HASH ?= "${STAGING_VERITY_DIR}/verity_roothash"

PACKAGE_INSTALL += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'root-hash-sign', 'openssl-bin', '', d)} \
"

deploy_verity_hash:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'root-hash-sign', 'true', 'false', d)}; then
        install -D -m 0644 \
            ${DM_VERITY_PUBLIC_KEY} \
            ${IMAGE_ROOTFS}${datadir}/misc/dm-verity-public-key
        install -D -m 0644 \
            ${DM_VERITY_ROOT_HASH}.signed \
            ${IMAGE_ROOTFS}${datadir}/misc/dm-verity-roothash-signed
        install -D -m 0644 \
            ${DM_VERITY_ROOT_HASH} \
            ${IMAGE_ROOTFS}${datadir}/misc/dm-verity-roothash
    fi
}

