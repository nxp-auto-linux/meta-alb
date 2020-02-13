# Copyright 2019-2020 NXP

DESCRIPTION = "ARM Trusted Firmware"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

# ATF repository
SRC_URI = "git://source.codeaurora.org/external/autobsps32/arm-trusted-firmware.git;protocol=https;branch=alb/master"
SRCREV ?= "40328123b7df1f8a959f2c5ea07982e2e033a1bc"

PLATFORM_s32g274aevb = "s32g"
BUILD_TYPE = "release"

ATF_BINARIES = "${B}/${PLATFORM}/${BUILD_TYPE}"

EXTRA_OEMAKE += "\
                CROSS_COMPILE=${TARGET_PREFIX} \
                ARCH=${TARGET_ARCH} \
                BUILD_BASE=${B} \
                PLAT=${PLATFORM} \
                "

do_compile() {
        unset LDFLAGS
        oe_runmake -C ${S}
}

do_deploy() {
        install -d ${DEPLOY_DIR_IMAGE}
        cp ${ATF_BINARIES}/bl2.bin ${DEPLOY_DIR_IMAGE}/bl2-${MACHINE}.bin
        cp ${ATF_BINARIES}/bl31.bin ${DEPLOY_DIR_IMAGE}/bl31-${MACHINE}.bin
}

addtask deploy before do_build after do_compile
do_deploy[depends] = "virtual/bootloader:do_deploy"

def pad_image(image):
    size = os.path.getsize(image)
    if size % 512 != 0:
        padding = 512 - (size % 512)
        os.system("dd if=/dev/zero bs=1 count={} >> {}".format(padding,image))
        size += padding
    return size

def write_ivt(output_file, addr, sizes):
    ivt_header_off = int("0x0", 16)
    ivt_app_boot_code_pointer_off = int("0x20", 16)
    ivt_boot_configuration_word_off = int("0x28", 16)
    dcd_header_off = int("0x200", 16)
    app_boot_code_header_off = int("0x2200", 16)
    ram_entry_pointer = addr[-1]
    ram_start_pointer = addr[0]
    code_length = addr[-1] + sizes[-1] - ram_start_pointer

    with open(output_file, "r+b") as fh:
        fh.seek(ivt_header_off)
        fh.write(bytes([0xd1, 0x01, 0x00, 0x60]))
        fh.seek(ivt_app_boot_code_pointer_off)
        fh.write(bytes(app_boot_code_header_off.to_bytes(4,byteorder = "little")))
        fh.seek(ivt_boot_configuration_word_off)
        fh.write(bytes([0x01, 0x00]))
        fh.seek(dcd_header_off)
        fh.write(bytes([0xd2, 0x00, 0x00, 0x60]))
        fh.seek(app_boot_code_header_off)
        fh.write(bytes([0xd5, 0x00, 0x00, 0x60]))
        fh.write(bytes(ram_start_pointer.to_bytes(4, byteorder = "little")))
        fh.write(bytes(ram_entry_pointer.to_bytes(4, byteorder = "little")))
        fh.write(bytes(code_length.to_bytes(4, byteorder = "little")))
    return

def write_images(output_file, images, addr, sizes):
    for i in range(len(images)-1):
        os.system("cat {} >> {}".format(images[i],output_file))
        endaddr = addr[i] + sizes[i]
        padding = addr[i+1] - endaddr
        padding = int(padding / 512)
        os.system("dd if=/dev/zero bs=512 count={} >> {}".format(padding, output_file))
    os.system("cat {} >> {}".format(images[-1],output_file))
    return

python do_add_atf_support () {
    from bb import data
    import sys, os

    bl2_file = d.getVar("ATF_BINARIES", True)+ "/" + "bl2.bin"
    bl31_file = d.getVar("ATF_BINARIES", True)+ "/" + "bl31.bin"
    uboot_file = d.getVar("DEPLOY_DIR_IMAGE", True) + "/u-boot.bin"
    output_file = d.getVar("ATF_IMAGE_OUTPUT", True)
    app_boot_code_code_off = int("0x2240", 16)

    images = []
    sizes = []
    addr = []
    images.append(uboot_file)
    addr.append(int(str(d.getVar("ATF_UBOOT_OFFSET", True)), 16))
    images.append(bl31_file)
    addr.append(int(str(d.getVar("ATF_BL31_OFFSET", True)), 16))
    images.append(bl2_file)
    addr.append(int(str(d.getVar("ATF_BL2_OFFSET", True)), 16))

    os.system("dd if=/dev/zero of={} bs=1 count={}".format(output_file, app_boot_code_code_off))

    for i in range(len(images)):
        sizes.append(pad_image(images[i]))

    write_ivt(output_file, addr, sizes)
    write_images(output_file, images, addr, sizes)

    return
}

COMPATIBLE_MACHINE = "s32g274aevb"
