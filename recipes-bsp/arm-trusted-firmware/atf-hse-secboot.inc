# Copyright 2023 NXP

DEPENDS += "arm-trusted-firmware-tools-native"

# Secure boot works only on sdcard
BOOT_TYPE = "sdcard"

FIP_BIN = "${ATF_BINARIES}/fip.bin-${BOOT_TYPE}"
FIP_S32 = "${ATF_BINARIES}/fip.s32-${BOOT_TYPE}"
FIPTOOL = "${STAGING_BINDIR_NATIVE}/fiptool"

RSA_PRIV = "${ATF_BINARIES}/fip_rsa2048_private.pem"
RSA_PUB = "${ATF_BINARIES}/fip_rsa2048_public.pem"

do_sign_fip () {
	openssl genrsa -out ${RSA_PRIV} 2048
	openssl rsa -in ${RSA_PRIV} -pubout -out ${RSA_PUB}

	${FIPTOOL} info ${FIP_BIN} > fiptool_info
	cert_off=$(sed -n  "s/.*Trusted Boot Firmware BL2 certificate: offset=\(0x[0-9A-Fa-f]*\).*/\1/p" fiptool_info)
	
	dd if=${FIP_BIN} of=tosign-fip.bin bs=1 count=`printf "%d" ${cert_off}` conv=notrunc,fsync
	openssl dgst -sha1 -sign ${RSA_PRIV} -out fip-signature.bin tosign-fip.bin

	${FIPTOOL} update --align 16 --tb-fw-cert fip-signature.bin ${FIP_BIN}

	fip_off=$(cat ${ATF_BINARIES}/fip_offset)
	dd if=${FIP_BIN} of=${FIP_S32} seek=`printf "%d" ${fip_off}` bs=1 oflag=seek_bytes conv=notrunc,fsync

	rm -f fiptool_info tosign-fip.bin fip-signature.bin
}

addtask do_sign_fip before do_deploy after do_compile

do_deploy:append() {
	cp -vf "${RSA_PRIV}" ${DEPLOYDIR}
	cp -vf "${RSA_PUB}" ${DEPLOYDIR}
}
