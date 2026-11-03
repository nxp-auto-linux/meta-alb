Yocto BSP layer for UC1.2
==========================================================================

hw-partition-1.2 contains one cohort layer:
* meta-alb-hw-partition-1.1.2 - generates Linux images for Cohort 2 which:
  - owns uSDHC and UFS,
  - owns SMMU
  - uses UART0 as console.

## Low Memory Layout:
> **NOTE:** All offsets in the table are relative to the DDR start address assigned to the cohort
> (0x80000000 for Cohort 2). The TF-A `DDR_START` build option and the start of the `memory` node
> in the device tree must both be set to this address.

| Region/Component           | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| DDR_START                  | 0x00000000                       |                                  |                                   |
| Reserved DMA Pool          | 0x00000000                       | 0x000FFFFF                       | 1 MB                              |
| HSE Reserved               | 0x10000000                       | 0x12FFFFFF                       | 48 MB                             |
| SCMI Shared Memory         | 0x13000000                       | 0x13000FFF                       | 4 KB                              |
| TLV Shared Memory          | 0x13002000                       | 0x13003FFF                       | 8 KB                              |
| Linux Device Tree          | 0x13100000                       | 0x131FFFFF                       | 1 MB, S32N_DTB_DDR_OFFSET         |
| Kernel Image               | 0x13200000                       |                                  | S32N_KERNEL_DDR_OFFSET            |
| RootFS (optional)          | 0x16000000                       |                                  |                                   |
| BL2 I/O Temp Buffer        | BL33_DTB - 0x2000                | BL33_DTB - 0x1001                | 4 KB, S32N_IO_BUFFER_SIZE         |
| BL33_DTB                   | BL33_ENTRYPOINT - BL33_DTB_SIZE  | BL33_ENTRYPOINT - 1              | DTB size (4 KB aligned) + 4 KB    |
| U-Boot (BL33_ENTRYPOINT)   | 0x7F7A0000                       |                                  | Not loaded with skip-uboot        |
| BL2                        | 0x7FF00000                       | 0x7FF3FFFF                       | 256 KB                            |
| BL31                       | 0x7FF41000                       | 0x7FFFFFFF                       | ~782 KB                           |
| DDR_END                    |                                  | 0x7FFFFFFF                       |                                   |

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

## High Memory Layout:
> **NOTE:** The DDR ranges listed below are dynamically initialized at runtime through the Linux kernel memory hotplug functionality.
>
> The exact ranges are discovered dynamically from FSS, and depend on the DDR ECC and Interleaving configurations.

### ECC enabled, interleaving enabled (default):
| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x8800000000                     | 0x8E7FFFFFFF                     | 26 GB                             |

### ECC enabled, interleaving disabled:
| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x8800000000                     | 0x8B3FFFFFFF                     | 13 GB                             |
| Region 1                   | 0xC000000000                     | 0xC33FFFFFFF                     | 13 GB                             |

### ECC disabled, interleaving enabled:
| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x8800000000                     | 0x8F7FFFFFFF                     | 30 GB                             |

### ECC disabled, interleaving disabled:
| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x8800000000                     | 0x8BBFFFFFFF                     | 15 GB                             |
| Region 1                   | 0xC000000000                     | 0xC3BFFFFFFF                     | 15 GB                             |
