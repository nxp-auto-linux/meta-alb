# Common settings for pcie based demos

CFLAGS ?= ""

DEMO_PCIE_S32V_BOARD_SETUP ??= "PCIE_SHMEM_EVB"

DEMO_PCIE_S32V_BOARD_SETUP_ls2 ?= "PCIE_SHMEM_BLUEBOXMINI"
DEMO_PCIE_S32V_BOARD_SETUP_s32v234bbmini ?= "PCIE_SHMEM_BLUEBOXMINI"

EP_LOCAL_DDR_ADDR ?= "0xC1100000"
RC_DDR_ADDR = "${@oe.utils.conditional("DEMO_PCIE_S32V_BOARD_SETUP", "PCIE_SHMEM_EVB", "${EP_LOCAL_DDR_ADDR}", "0x8080100000", d)}"

# update shared memory addresses with the ones in dtb (second Mb)
CFLAGS_append += "${@bb.utils.contains('DISTRO_FEATURES', 'pcie-demos-support', ' -D${DEMO_PCIE_S32V_BOARD_SETUP} -DRC_DDR_ADDR=${RC_DDR_ADDR} -DEP_LOCAL_DDR_ADDR=${EP_LOCAL_DDR_ADDR}', '', d)}"
# add -DENABLE_DUMP if needed

EXTRA_OEMAKE += "S32V_BOARD_SETUP=${DEMO_PCIE_S32V_BOARD_SETUP} CFLAGS="${CFLAGS}" V=1"

# we support BlueBox (s32v234pcie and ls2080abluebox) BlueBox Mini (s32v234bbmini and ls2084abbmini) and s32v234evb
COMPATIBLE_MACHINE = "s32v234pcie|s32v234evb|s32v234bbmini|ls2080abluebox|ls2084abbmini|s32v234sbc|s32v234campp|lx2160abluebox3|s32v234hpcsom"
