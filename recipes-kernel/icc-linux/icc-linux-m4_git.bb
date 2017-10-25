SUMMARY = "ICC Linux for s32v234 - M4 (RTOS) to A53 (Linux)"

require icc-linux.inc
SRCBRANCH = "develop"
SRCREV = "355de47db535f72be57ca6716fbc82c06932d1c2"

EXTRA_OEMAKE = "CONFIG=RTOS"

M4_SAMPLE_DIR = "/opt/AUTOSAR"
FILES_${PN} = "${M4_SAMPLE_DIR}"
