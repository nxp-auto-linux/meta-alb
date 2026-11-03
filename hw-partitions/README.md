Yocto BSP layers for the S32N79 hardware partitions
==========================================================================

Each hw-partition-* directory contains the layers that generate the Linux images of the cohorts of
one hardware partition. The README of each directory lists its cohorts, the Low memory assigned to
them and their High Memory Layout. The Low Memory Layout described below is shared by all of them.

## Low Memory Layout:
> **NOTE:** All offsets in the table are relative to `DDR_START`, the first address of the Low
> memory assigned to the cohort. The components at the top of this memory are placed relative to
> `DDR_END`, its last address. Both addresses are listed in the README of each hardware partition.
> The TF-A `DDR_START` and `DDR_END` build options and the `memory` node in the device tree must
> all describe the same range.

| Region/Component           | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| DDR_START                  | 0x00000000                       |                                  |                                   |
| Reserved DMA Pool          | 0x00000000                       | 0x000FFFFF                       | Up to 1 MB, size per cohort       |
| HSE Reserved               | 0x10000000                       | 0x12FFFFFF                       | 48 MB                             |
| SCMI Shared Memory         | 0x13000000                       | 0x13000FFF                       | 4 KB                              |
| TLV Shared Memory          | 0x13002000                       | 0x13003FFF                       | 8 KB                              |
| Linux Device Tree          | 0x13100000                       | 0x131FFFFF                       | 1 MB, S32N_DTB_DDR_OFFSET         |
| Kernel Image               | 0x13200000                       |                                  | S32N_KERNEL_DDR_OFFSET            |
| RootFS (optional)          | 0x16000000                       |                                  |                                   |
| BL2 I/O Temp Buffer        | BL33_DTB - 0x2000                | BL33_DTB - 0x1001                | 4 KB, S32N_IO_BUFFER_SIZE         |
| BL33_DTB                   | BL33_ENTRYPOINT - BL33_DTB_SIZE  | BL33_ENTRYPOINT - 1              | DTB size (4 KB aligned) + 4 KB    |
| U-Boot (BL33_ENTRYPOINT)   | DDR_END - 0x85FFFF               |                                  | Not loaded with skip-uboot        |
| BL2                        | DDR_END - 0xFFFFF                | DDR_END - 0xC0000                | 256 KB                            |
| BL31                       | DDR_END - 0xBEFFF                | DDR_END                          | ~782 KB                           |

### Linux Kernel Image and Device Tree placement:
The Linux Kernel Image and Linux Device Tree offsets are set by the `S32N_KERNEL_DDR_OFFSET` and
`S32N_DTB_DDR_OFFSET` machine variables.
Both boot flows use the same values, so the OS memory layout does not depend on whether U-Boot is
skipped:
* With U-Boot, TF-A loads U-Boot at `BL33_ENTRYPOINT` and its device tree at `BL33_DTB`, right
  below it. U-Boot then loads the Linux Kernel Image at `S32N_KERNEL_DDR_OFFSET`
  (`CONFIG_SYS_LOAD_RAM_OFFSET`, `loadaddr`) and the Linux Device Tree at `S32N_DTB_DDR_OFFSET`
  (`CONFIG_DTB_RAM_OFFSET`, `fdt_addr`).
* With the `skip-uboot` distro feature, U-Boot is not loaded and `BL33_ENTRYPOINT` is the Linux
  Kernel Image, at `S32N_KERNEL_DDR_OFFSET` (TF-A `BL33_ENTRYPOINT_DDR_START_OFFSET`). `BL33_DTB`,
  which is now the Linux Device Tree, and the BL2 I/O Temp Buffer move right below the Kernel
  Image, inside the Linux Device Tree region. BL2 stops the boot if the I/O Temp Buffer starts
  below `S32N_DTB_DDR_OFFSET` (TF-A `BL33_DTB_DDR_START_OFFSET`), so the Linux Device Tree,
  rounded up to 4 KB, can be at most 0xFD000 bytes.

To move the OS images, override both variables. The Linux Device Tree region spans from
`S32N_DTB_DDR_OFFSET` up to `S32N_KERNEL_DDR_OFFSET` and must not overlap the other regions above.
