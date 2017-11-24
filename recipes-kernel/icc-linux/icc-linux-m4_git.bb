SUMMARY = "ICC Linux for s32v234 - M4 (RTOS) to A53 (Linux)"

require icc-linux.inc
SRCREV = "5abc7e8e87532bb1603ee84511b299b104489fde"

EXTRA_OEMAKE = "CONFIG=RTOS"

#M4_SAMPLE_DIR = "/opt/AUTOSAR"
#FILES_${PN} = "${M4_SAMPLE_DIR}"
