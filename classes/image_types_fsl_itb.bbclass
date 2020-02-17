#
# (C)2015-1017 NXP Semiconductors
# <Heinz.Wrobel@nxp.com>
#
# This class is used to create an itb file for the current
# kernel/dtb/rootfs configuration.
# It may also be used by normal recipes to generate a kernel itb
# but in that case it is suggested to set ITB_DEPLOY_SUFFIX="" to
# inhibit the image based deployment mechanism.
#
inherit image_types
IMAGE_TYPES += "itb"

# The itb requires the rootfs filesystem to be built before using
# it so we must make this dependency explicit.
# The user is responsible to ensure the complete dependency handling!
#ITB_ROOTFS_BASENAME = "${IMAGE_BASENAME}"
#ITB_ROOTFS_TYPE = "ext3"
#ITB_ROOTFS_COMPRESSION = "gz"
ITB_ROOTFS_BASENAME ?= ""
ITB_ROOTFS_TYPE ?= ""
ITB_ROOTFS_COMPRESSION ?= "none"

ITB_ROOTFS_SUFFIX ?= '${ITB_ROOTFS_TYPE}${@oe.utils.conditional("ITB_ROOTFS_COMPRESSION", "none", "", ".${ITB_ROOTFS_COMPRESSION}", d)}'
ITB_ROOTFS_REALSUFFIX ?= '${@oe.utils.conditional("ITB_ROOTFS_SUFFIX", "", "", ".${ITB_ROOTFS_SUFFIX}", d)}'
ITB_ROOTFS_DIR ?= "${DEPLOY_DIR_IMAGE}"
ITB_ROOTFS_DIR_SLASH ?= "${ITB_ROOTFS_DIR}/"
ITB_ROOTFS_BASENAME_MACHINE ?= "${ITB_ROOTFS_BASENAME}-${MACHINE}"
IMAGE_TYPEDEP_itb ?= "${ITB_ROOTFS_SUFFIX}"

#do_image_itb[depends] += 'u-boot-mkimage-native:do_populate_sysroot virtual/kernel:do_deploy ${@oe.utils.conditional("ITB_ROOTFS_TYPE", "", "", "${ITB_ROOTFS_BASENAME}:do_image_${ITB_ROOTFS_TYPE}", d)}'
do_image_itb[depends] += 'u-boot-mkimage-native:do_populate_sysroot virtual/kernel:do_deploy ${ITB_ROOTFS_BASENAME}:do_image_complete'

# The ITB is defined by the image we are building, so we name it
# based on the image.
# The other defaults are derived from Layerscape aarch64 and may well need
# overrides for other architectures
ITB_ITS_FILE ?= "${S}/${IMAGE_NAME}.its"
ITB_DEPLOY_SUFFIX ?= ".itb"
ITB_DEPLOY_ITBNAME ?= "${IMAGE_NAME}"
ITB_DEPLOY_LINK_ITBNAME ?= "${IMAGE_LINK_NAME}"
ITB_DEPLOYDIR ?= "${DEPLOY_DIR_IMAGE}"
ITB_ARCH ?= "${@bb.utils.contains("TARGET_ARCH", "aarch64", "arm64", "${TARGET_ARCH}", d)}"
UBOOT_LOADADDRESS ?= "${UBOOT_ENTRYPOINT}"
ITB_KERNEL_LOAD  ?= "${UBOOT_LOADADDRESS}"
ITB_KERNEL_ENTRY ?= "${UBOOT_ENTRYPOINT}"
ITB_DTB_LOAD     ?= "${DTB_LOAD}"

#
# Create an itb image
#
generate_itb() {
        gzip -c ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin >${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.gz

        IIF="${ITB_ITS_FILE}"
        if [ -n "${KERNEL_ITS_FILE}" ]; then
                IIF="${KERNEL_ITS_FILE}"
        fi
        mkimage -f ${IIF} ${S}/kernel.itb

	# Deploying an image can be suppressed, and/or the name
	# can be customized
        if [ -n "${ITB_DEPLOYDIR}" ]; then
		install -d ${ITB_DEPLOYDIR}
		install -m 644 ${S}/kernel.itb ${ITB_DEPLOYDIR}/${ITB_DEPLOY_ITBNAME}${ITB_DEPLOY_SUFFIX}
		ln -sf ${ITB_DEPLOY_ITBNAME}${ITB_DEPLOY_SUFFIX} ${ITB_DEPLOYDIR}/${ITB_DEPLOY_LINK_ITBNAME}${ITB_DEPLOY_SUFFIX}
        fi

}

IMAGE_CMD_itb () {
        # If no ITB_ITS_FILE is specified, we create a default one
        IIF="${ITB_ITS_FILE}"
        IIFWRITE=1
        if [ -n "${KERNEL_ITS_FILE}" ]; then
                IIF="${KERNEL_ITS_FILE}"
                IIFWRITE=0
        fi

        compression="none"
        case ${ITB_ROOTFS_COMPRESSION} in
                gz)
                        compression="gzip"
                        ;;
                bz2)
                        compression="bzip2"
                        ;;
                lzma)
                        compression="lzma"
                        ;;
                lzo)
                        compression="lzo"
                        ;;
                lz4)
                        compression="lz4"
                        ;;
        esac


        if [ ${IIFWRITE} ]; then
                echo >${IIF}  "/*"
                echo >>${IIF} " * Copyright (C) 2015-2017, NXP"
                echo >>${IIF} " */"
                echo >>${IIF} ""
                echo >>${IIF} "/dts-v1/;"
                echo >>${IIF} ""
                echo >>${IIF} "/ {"
                echo >>${IIF} "    description = \"${MACHINE} Image file for the Linux Kernel\";"
                echo >>${IIF} "    #address-cells = <1>;"
                echo >>${IIF} ""
                echo >>${IIF} "    images {"
                echo >>${IIF} "        kernel@1 {"
                echo >>${IIF} "            description = \"${ITB_ARCH} Linux kernel\";"
                echo >>${IIF} "            data = /incbin/(\"${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.gz\");"
                echo >>${IIF} "            type = \"kernel\";"
                echo >>${IIF} "            arch = \"${ITB_ARCH}\";"
                echo >>${IIF} "            os = \"linux\";"
                echo >>${IIF} "            compression = \"gzip\";"
                echo >>${IIF} "            load = <${ITB_KERNEL_LOAD}>;"
                echo >>${IIF} "            entry = <${ITB_KERNEL_ENTRY}>;"
                echo >>${IIF} "        };"
                echo >>${IIF} "        fdt@1 {"
                echo >>${IIF} "            description = \"Flattened Device Tree blob\";"
                DTS_BASE_NAME=`basename ${KERNEL_DEVICETREE} | awk -F "." '{print $1}'`
                echo >>${IIF} "            data = /incbin/(\"${DEPLOY_DIR_IMAGE}/${DTS_BASE_NAME}.dtb\");"
                echo >>${IIF} "            type = \"flat_dt\";"
                echo >>${IIF} "            arch = \"${ITB_ARCH}\";"
                echo >>${IIF} "            compression = \"none\";"
                echo >>${IIF} "            load = <${ITB_DTB_LOAD}>;"
                echo >>${IIF} "        };"
		if [ -n "${ITB_ROOTFS_TYPE}" ]; then
			echo >>${IIF} "        ramdisk@1 {"
			echo >>${IIF} "            description = \"${MACHINE} Ramdisk\";"
			echo >>${IIF} "            data = /incbin/(\"${ITB_ROOTFS_DIR_SLASH}${ITB_ROOTFS_BASENAME_MACHINE}${ITB_ROOTFS_REALSUFFIX}\");"
			echo >>${IIF} "            type = \"ramdisk\";"
			echo >>${IIF} "            arch = \"${ITB_ARCH}\";"
			echo >>${IIF} "            os = \"linux\";"
			echo >>${IIF} "            compression = \"${compression}\";"
			echo >>${IIF} "        };"
                fi
                echo >>${IIF} "    };"
                echo >>${IIF} ""
                echo >>${IIF} "    configurations {"
                echo >>${IIF} "            default = \"config@1\";"
                echo >>${IIF} "            config@1 {"
                echo >>${IIF} "                    description = \"Boot Linux kernel\";"
                echo >>${IIF} "                    kernel = \"kernel@1\";"
                echo >>${IIF} "                    fdt = \"fdt@1\";"
		if [ -n "${ITB_ROOTFS_TYPE}" ]; then
			echo >>${IIF} "                    ramdisk = \"ramdisk@1\";"
                fi
                echo >>${IIF} "            };"
                echo >>${IIF} "    };"
                echo >>${IIF} "};"
        fi

        generate_itb
}


