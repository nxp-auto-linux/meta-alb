# Copyright 2019-2020 NXP

DESCRIPTION = "ARM Trusted Firmware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

DEPENDS += "dtc-native xxd-native"
DEPENDS += "openssl-native"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

# ATF repository
SRC_URI = "git://source.codeaurora.org/external/autobsps32/arm-trusted-firmware.git;protocol=https;branch=alb/master"
SRCREV ?= "a06585aedc191f8cc77f19039c3ed324058bbb4e"

SRC_URI += "file://0001-Fix-fiptool-build-error.patch"

PLATFORM_s32g274aevb = "s32g"
PLATFORM_s32g274ardb = "s32g"
BUILD_TYPE = "release"

ATF_BINARIES = "${B}/${PLATFORM}/${BUILD_TYPE}"

BL33_DUMMY = "${B}/bl33_dummy"

EXTRA_OEMAKE += " \
                CROSS_COMPILE=${TARGET_PREFIX} \
                ARCH=${TARGET_ARCH} \
                BUILD_BASE=${B} \
                PLAT=${PLATFORM} \
                "

# FIXME: Allow linking of 'tools' binaries with native libraries
#        used for generating the boot logo and other tools used
#        during the build process.
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CPPFLAGS}" \
                 HOSTLD="${BUILD_LD} -L${STAGING_BASE_LIBDIR_NATIVE} \
                 -Wl,-rpath,${STAGING_LIBDIR_NATIVE} \
                 -Wl,-rpath,${STAGING_BASE_LIBDIR_NATIVE}" \
                 LIBPATH="${STAGING_LIBDIR_NATIVE}" \
                 HOSTSTRIP=true'

do_compile() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	# fiptool (currently being integrated) requires a BL33 image; until we
	# finish integration, we'll just pass some dummy file, to appease the
	# script; it will not be used in the actual SD card image
	echo 1 > "${BL33_DUMMY}"
	oe_runmake -C ${S} BL33="${BL33_DUMMY}" all
	rm -rf "${BL33_DUMMY}"
}

do_deploy() {
	install -d ${DEPLOY_DIR_IMAGE}
	cp -v ${ATF_BINARIES}/bl2.bin ${DEPLOY_DIR_IMAGE}/bl2-${MACHINE}.bin
	cp -v ${ATF_BINARIES}/bl31.bin ${DEPLOY_DIR_IMAGE}/bl31-${MACHINE}.bin
	# Copy the dtb with a different name, to avoid name clashes with the kernel dtb
	cp -v ${ATF_BINARIES}/fdts/s32g274aevb.dtb ${DEPLOY_DIR_IMAGE}/fsl-${MACHINE}-atf.dtb
	mv -v ${TOPDIR}/${ATF_IMAGE_FILE} ${DEPLOY_DIR_IMAGE}/${ATF_IMAGE_FILE}
}

addtask deploy before do_build after do_add_atf_support
addtask add_atf_support after do_compile before do_deploy
do_add_atf_support[depends] = "virtual/bootloader:do_install"

def write_ivt(output_file, abc_address):
    ivt_header_off = int("0x0", 16)
    ivt_app_boot_code_pointer_off = int("0x20", 16)
    ivt_boot_configuration_word_off = int("0x28", 16)

    with open(output_file, "w") as fh:
        fh.write('\0' * 256)

    with open(output_file, "r+b") as fh:
        fh.seek(ivt_header_off)
        fh.write(bytes([0xd1, 0x01, 0x00, 0x60]))
        fh.seek(ivt_app_boot_code_pointer_off)
        fh.write(bytes(abc_address.to_bytes(4, byteorder = "little")))
        fh.seek(ivt_boot_configuration_word_off)
        fh.write(bytes([0x01, 0x00]))
    return

def write_abc(rsp, rep, bin, dtb, output_file, append):
    dtb_reserved_size = int("0x2000", 16)
    code_length = os.path.getsize(bin)
    if dtb is not None:
        dtb_size = os.path.getsize(dtb)
        if dtb_size > dtb_reserved_size:
            raise Exception("ATF DTB is too large!")
        code_length += dtb_reserved_size

    with open(output_file, "wb") as fh:
        fh.write(bytes([0xd5, 0x00, 0x00, 0x60]))
        fh.write(bytes(rsp.to_bytes(4, byteorder = "little")))
        fh.write(bytes(rep.to_bytes(4, byteorder = "little")))
        fh.write(bytes(code_length.to_bytes(4, byteorder = "little")))
        fh.write(bytes([0]) * 48)

    if append:
        if dtb is not None:
            os.system('dd if={} >> {}'.format(dtb, output_file))
            padding = dtb_reserved_size - dtb_size
            os.system('dd if=/dev/zero bs=1 count={} >> {}'.format(padding, output_file))
        os.system('dd if={} >> {}'.format(bin, output_file))
    return

python do_add_atf_support () {
    from bb import data
    import sys, os, re

    tmp = "./_atf_tmp_"
    os.system("rm -rf {}".format(tmp))
    os.system("mkdir -p {}".format(tmp))

    bl2_file = d.getVar("ATF_BINARIES", True) + "/" + "bl2.bin"
    bl31_file = d.getVar("ATF_BINARIES", True) + "/" + "bl31.bin"
    # Currently there is only one dtb for both RDB and EVB, and its name is
    # hard-coded based on EVB platform.
    atf_dtb_file = d.getVar("ATF_BINARIES", True) + "/" + "fdts/s32g274aevb.dtb"
    uboot_file = d.getVar("DEPLOY_DIR_IMAGE", True) + "/u-boot.bin"

    output_file = str(d.getVar("ATF_IMAGE_FILE", True))

    # Copy all images into one place
    dst = tmp
    os.system("cp {} {} -vf".format(bl2_file, dst))
    os.system("cp {} {} -vf".format(bl31_file, dst))
    os.system("cp {} {} -vf".format(atf_dtb_file, dst))
    os.system("cp {} {} -vf".format(uboot_file, dst))

    # Generate IVT
    dst = tmp + "/" + "ivt.sdcard.0"
    write_ivt(dst, int(str(d.getVar("ATF_BL2_MMC_OFFSET", True)), 16))

    # Generate BL2 image
    rsp = int(str(d.getVar("ATF_BL2_LOADADDR", True)), 16)
    rep = int(str(d.getVar("ATF_BL2_ENTRYPOINT", True)), 16)
    dst = tmp + "/" + "bl2.sdcard." + d.getVar("ATF_BL2_MMC_OFFSET", True)
    bin = tmp + "/" + "bl2.bin"
    dtb = tmp + "/" + "s32g274aevb.dtb"
    write_abc(rsp, rep, bin, dtb, dst, True)

    # erate BL31 image
    src = tmp + "/" + "bl31.bin"
    dst = tmp + "/" + "bl31.sdcard." + d.getVar("ATF_BL31_MMC_OFFSET", True)
    os.system("dd if={} of={} status=none".format(src, dst))

    # Generate u-boot image
    rsp = int(str(d.getVar("ATF_UBOOT_ENTRYPOINT", True)), 16)
    rep = rsp
    bin = tmp + "/" + "u-boot.bin"
    dst = tmp + "/" + "uboot_app_boot_code.bin"
    write_abc(rsp, rep, bin, None, dst, False)
    src = tmp + "/" + "uboot_app_boot_code.bin"
    dst = tmp + "/" + "uboot.sdcard." + d.getVar("ATF_UBOOT_MMC_OFFSET", True)
    os.system("dd if={} status=none >> {}".format(src, dst))
    src = tmp + "/" + "u-boot.bin"
    os.system("dd if={} status=none >> {}".format(src, dst))


    files = os.listdir(tmp)
    for f in files:
        matchObj = re.match('.*\.([0-9a-f]+)$', f, re.M | re.I)
        if matchObj:
            src = tmp + "/" + matchObj.group()
            offset = matchObj.group(1)
            size = os.path.getsize(src)
            os.system("dd if={} of={} bs=$((0x1000)) seek=$(({})) oflag=seek_bytes conv=fsync,notrunc &> /dev/null".format(
                      src, output_file, "0x" + offset))
    return
}

COMPATIBLE_MACHINE = "s32g274aevb|s32g274ardb"
