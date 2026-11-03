Yocto BSP layer for UC1.1
==========================================================================

hw-partition-1.1 contains two cohort layers:
* meta-alb-hw-partition-1.1.5 - generates Linux images for Cohort 5 which:
  - boots from uSDHC,
  - owns SMMU,
  - owns UFS HCI2,
  - uses UART0 as console.
* meta-alb-hw-partition-1.1.6 - generates Linux images for Cohort 6 which:
  - boots from UFS using HCI3,
  - uses UART7 as console.

## Low Memory Layout:
Each cohort has 0x1c000000 bytes assigned space in Low memory. The split of this space is common to
all the hardware partitions and is described in the [Low Memory Layout](../README.md#low-memory-layout).

| Cohort                     | DDR_START                        | DDR_END                          | Reserved DMA Pool                 |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Cohort 5 (1.1.5)           | 0x80000000                       | 0x9BFFFFFF                       | 1 MB                              |
| Cohort 6 (1.1.6)           | 0x9C000000                       | 0xB7FFFFFF                       | 512 KB                            |

## High Memory Layout:
> **NOTE:** The DDR ranges listed below are dynamically initialized at runtime through the Linux kernel memory hotplug functionality.
>
> The exact ranges are discovered dynamically from FSS, and depend on the DDR ECC and Interleaving configurations.

### Cohort 5 (hw-partition-1.1.5):
#### Interleaving enabled (default):
| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x8800000000                     | 0x8B7FFFFFFF                     | 14 GB                             |

#### Interleaving disabled:
| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x8800000000                     | 0x89BFFFFFFF                     | 7 GB                              |
| Region 1                   | 0xC000000000                     | 0xC1BFFFFFFF                     | 7 GB                              |

### Cohort 6 (hw-partition-1.1.6):
#### Interleaving enabled (default):
| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x8B80000000                     | 0x8E7FFFFFFF                     | 12 GB                             |

#### Interleaving disabled:
| DDR Range                  | Start                            | End                              | Note                              |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Region 0                   | 0x89C0000000                     | 0x8B3FFFFFFF                     | 6 GB                              |
| Region 1                   | 0xC1C0000000                     | 0xC33FFFFFFF                     | 6 GB                              |
