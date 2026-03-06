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
| Linux CMA                  | 0x00000000                       | 0x0FFFFFFF                       | 256 MB                            |
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
