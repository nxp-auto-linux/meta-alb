#
# This class is meant to initialize the QSPI Flash offsets environment
# variables based on individual components partitions read from
# the Linux Kernel FDT.
# It only applies to S32CC platforms.
#

DEPENDS += "python-fdt-native"

def add_fdt_library_path(d):
    import glob, os, sys

    staging_dir_host_path = d.getVar('STAGING_DIR_NATIVE') or ""
    if not staging_dir_host_path:
        raise Exception("'STAGING_DIR_NATIVE' environment variable is not defined")

    fdt_lib_path = os.path.join(staging_dir_host_path, "usr/lib/python3.*/site-packages/")
    fdt_lib_path = glob.glob(fdt_lib_path)
    if len(fdt_lib_path) == 0:
        raise Exception("Path to 'python-fdt' library not found")
    elif len(fdt_lib_path) > 1:
        raise Exception("Multiple paths to 'python-fdt' library detected")

    fdt_lib_path = fdt_lib_path[0]
    sys.path.insert(0, fdt_lib_path)

python update_flash_offsets () {
    add_fdt_library_path(d)
    import fdt

    kernel_fdt = d.getVar('FLASHIMAGE_DTB_FILE') or ""
    fdt_path = d.getVar('DEPLOY_DIR_IMAGE') or ""
    fdt_path = os.path.join(fdt_path, kernel_fdt)

    with open(fdt_path, "rb") as fdt_file:
        linux_dt = fdt.parse_dtb(fdt_file.read())

    qspi_node_alias = "spi6"
    qspi_node_path_list = linux_dt.search(name=qspi_node_alias, itype=fdt.ItemType.PROP, path="aliases", recursive=False)
    if not qspi_node_path_list:
        raise Exception(qspi_node_alias + " alias not found")
    qspi_node_path = qspi_node_path_list[0].value

    qspi_part_node_name = "partitions"
    flash_part_list	= linux_dt.search(name=qspi_part_node_name, itype=fdt.ItemType.NODE, path=qspi_node_path)
    if not flash_part_list:
        raise Exception(qspi_part_node_name + " node not found in file: " + fdt_path)
    flash_part_node = flash_part_list[0]

    flash_map = {'FIP' : 'FLASHIMAGE_FIP_OFFSET',
                 'U-Boot-Env' : 'FLASHIMAGE_UBOOT_ENV_OFFSET',
                 'Kernel' : 'FLASHIMAGE_KERNEL_OFFSET',
                 'DTB' : 'FLASHIMAGE_DTB_OFFSET',
                 'Rootfs' : 'FLASHIMAGE_ROOTFS_OFFSET',
                 'PFE-Firmware' : 'FLASHIMAGE_PFE_OFFSET'}

    for subnode in flash_part_node.nodes:
        label = subnode.get_property("label")[0]
        if label in flash_map.keys():
            offset = hex(subnode.get_property("reg")[0])
            d.setVar(flash_map[label], offset)
}
