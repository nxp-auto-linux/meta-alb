require conf/machine/include/s32-sim-base.inc
require conf/machine/include/s32n-base.inc

SDCARD_SIMIMAGE ?= "${DEPLOY_DIR_IMAGE}/simimage/${IMAGE_BASENAME}.sdcard"
UFS_LUN0_SIMIMAGE ?= "${DEPLOY_DIR_IMAGE}/simimage/${IMAGE_BASENAME}.lun0.ufs"
UFS_LUN1_SIMIMAGE ?= "${DEPLOY_DIR_IMAGE}/simimage/${IMAGE_BASENAME}.lun1.ufs"
