#
# Copyright 2023, 2025 NXP
#

inherit_defer dm-verity-img

DM_VERITY_PRIVATE_KEY ?= "${DEPLOY_DIR_IMAGE}/verity_private.pem"
DM_VERITY_PUBLIC_KEY ?= "${DEPLOY_DIR_IMAGE}/verity_public.pem"
DM_VERITY_ROOT_HASH ?= "${STAGING_VERITY_DIR}/verity_roothash"

DEPENDS += "openssl-native cryptsetup-native"

generate_verity_keys() {
    if [ ! -f "${DM_VERITY_PRIVATE_KEY}" ]; then
        openssl genrsa -out "${DM_VERITY_PRIVATE_KEY}" 2048
        openssl rsa -pubout -in "${DM_VERITY_PRIVATE_KEY}" -out "${DM_VERITY_PUBLIC_KEY}"
    fi

    local ENV="${STAGING_VERITY_DIR}/${IMAGE_BASENAME}.${DM_VERITY_IMAGE_TYPE}.verity.env"
    VAL=$(grep "ROOT_HASH" "$ENV" | cut -d "=" -f2)
    if [ -z "$VAL" ]; then
        bbfatal "Root hash not found. Signing failed.\n"
    fi

    printf '%s' "$VAL" | tr -d '\n' > "${DM_VERITY_ROOT_HASH}"
    openssl dgst -sha256 -sign "${DM_VERITY_PRIVATE_KEY}" -out "${DM_VERITY_ROOT_HASH}.signed" "${DM_VERITY_ROOT_HASH}"
}

CONVERSION_CMD:verity:append = ";generate_verity_keys"