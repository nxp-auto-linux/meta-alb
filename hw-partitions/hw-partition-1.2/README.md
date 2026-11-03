Yocto BSP layer for UC1.2
==========================================================================

hw-partition-1.2 contains one cohort layer:
* meta-alb-hw-partition-1.1.2 - generates Linux images for Cohort 2 which:
  - owns uSDHC and UFS,
  - owns SMMU
  - uses UART0 as console.

## Low Memory Layout:
The cohort has 0x80000000 bytes assigned space in Low memory. The split of this space is common to
all the hardware partitions and is described in the [Low Memory Layout](../README.md#low-memory-layout).

| Cohort                     | DDR_START                        | DDR_END                          | Reserved DMA Pool                 |
|----------------------------|----------------------------------|----------------------------------|-----------------------------------|
| Cohort 2 (1.2.1)           | 0x80000000                       | 0xFFFFFFFF                       | 1 MB                              |

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
