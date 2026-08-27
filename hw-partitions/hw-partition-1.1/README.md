Yocto BSP layer for UC1.1
==========================================================================

hw-partition-1.1 contains two cohort layers:
* meta-alb-hw-partition-1.1.5 - generates Linux images for Cohort 5 which:
  - boots from uSDHC,
  - owns SMMU,
  - uses UART0 as console.
* meta-alb-hw-partition-1.1.6 - generates Linux images for Cohort 6 which:
  - boots from UFS,
  - uses UART7 as console.

## Low Memory Layout:
Each cohort has 0x1c000000 bytes assigned space in Low memory, split as follows:
> **NOTE:** All offsets in the table are relative to the DDR start address assigned to the cohort

| Region/Component           | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| DDR_START                  | 0x00000000                       |                                  |                                   |
| Reserved DMA Pool          | 0x00000000                       | 0x000FFFFF                       | 1 MB (Cohort 6: 512 KB)           |
| HSE Reserved               | 0x10000000                       | 0x12FFFFFF                       | 48 MB                             |
| SCMI Shared Memory         | 0x13000000                       | 0x13000FFF                       | 4 KB                              |
| TLV Shared Memory          | 0x13002000                       | 0x13003FFF                       | 8 KB                              |
| Linux Device Tree          | 0x13100000                       | 0x131FFFFF                       | ~2 MB                             |
| Kernel Image               | 0x13200000                       |                                  |                                   |
| RootFS (optional)          | 0x16000000                       |                                  |                                   |
| BL2 I/O Temp Buffer        | BL33_DTB - 0x100000              | BL33_DTB - 1                     | S32N_IO_BUFFER_SIZE = 0x100000;   |
| BL33_DTB                   | BL33_ENTRYPOINT - BL33_DTB_SIZE  | BL33_ENTRYPOINT - 1              | BL33_DTB_SIZE is rounded up to 4K |
| U-Boot (BL33_ENTRYPOINT)   | 0x1B7A0000                       |                                  |                                   |
| BL2                        | 0x1BF00000                       | 0x1BF3FFFF                       | 256 KB                            |
| BL31                       | 0x1BF41000                       | 0x1BFFFFFF                       | ~782 KB                           |
| DDR_END                    |                                  | 0x1BFFFFFF                       |                                   |

## High Memory Layout:
> **NOTE:** The DDR ranges listed below are dynamically initialized at runtime through the Linux kernel memory hotplug functionality.
>
> The Cohort 6 ranges depend on the DDR ECC configuration selected through the `DDR_ECC_SUPPORT`.
> The extra memory gained by disabling ECC is entirely assigned to Cohort 6, so the Cohort 5 layout is identical in
> both configurations. The two Cohort 6 configurations map to the `ecc-on` and `ecc-off` DDR device tree fragments
> under `meta-alb-hw-partition-1.1.6/recipes-bsp/dts-ddr/`.

### Cohort 5 (hw-partition-1.1.5):
> **NOTE:** Unaffected by `DDR_ECC_SUPPORT`.

| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x8800000000                     | 0x89BFFFFFFF                     | 7 GB                              |
| Region 1                   | 0xC000000000                     | 0xC1BFFFFFFF                     | 7 GB                              |

### Cohort 6 (hw-partition-1.1.6):
#### ECC enabled (`DDR_ECC_SUPPORT = "1"`, default):
| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x89C0000000                     | 0x8B3FFFFFFF                     | 6 GB                              |
| Region 1                   | 0xC1C0000000                     | 0xC33FFFFFFF                     | 6 GB                              |

#### ECC disabled (`DDR_ECC_SUPPORT = "0"`):
| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x89C0000000                     | 0x8BBFFFFFFF                     | 8 GB                              |
| Region 1                   | 0xC1C0000000                     | 0xC3BFFFFFFF                     | 8 GB                              |
