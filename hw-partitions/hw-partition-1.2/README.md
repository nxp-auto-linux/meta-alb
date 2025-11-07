Yocto BSP layer for UC1.2
==========================================================================

hw-partition-1.2 contains one cohort layer:
* meta-alb-hw-partition-1.1.2 - generates Linux images for Cohort 2 which:
  - owns uSDHC and UFS,
  - owns SMMU
  - uses UART0 as console.

## Low Memory Layout:
> **NOTE:** All offsets in the table are relative to the DDR start address assigned to the cohort

| Region/Component           | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| DDR_START                  | 0x00000000                       |                                  |                                   |
| Linux CMA                  | 0x00000000                       | 0x0FFFFFFF                       | 256 MB                            |
| HSE Reserved               | 0x10000000                       | 0x11FFFFFF                       | 32 MB                             |
| SCMI Shared Memory         | 0x13000000                       | 0x13000FFF                       | 4 KB                              |
| Linux Device Tree          | 0x13100000                       | 0x131FFFFF                       | ~2 MB                             |
| Kernel Image               | 0x13200000                       |                                  |                                   |
| RootFS (optional)          | 0x16000000                       |                                  |                                   |
| BL2 I/O Temp Buffer        | BL33_DTB - 0x100000              | BL33_DTB - 1                     | S32N_IO_BUFFER_SIZE = 0x100000;   |
| BL33_DTB                   | BL33_ENTRYPOINT - BL33_DTB_SIZE  | BL33_ENTRYPOINT - 1              | BL33_DTB_SIZE is rounded up to 4K |
| U-Boot (BL33_ENTRYPOINT)   | 0xFF7A0000                       |                                  |                                   |
| BL2                        | 0xFFF00000                       | 0xFFF3FFFF                       | 256 KB                            |
| BL31                       | 0xFFF41000                       | 0xFFFFFFFF                       | ~782 KB                           |
| DDR_END                    |                                  | 0xFFFFFFFF                       |                                   |
