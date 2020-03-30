inherit linux-kernel-base

# KERNEL_VERSION is inherited from .bb file

# override default deploy location for ubuntu to match ubuntu's convention for kernel source dir 
KERNEL_SOURCE_DIR_ubuntu = "${KERNEL_VERSION}"
KERNEL_SRC_PATH_ubuntu = "/usr/src/kernel"

# deploy 'linux-headers-<version>-generic as symlink to KERNEL_SRC_PATH'
KERNEL_HEADERS_DIR_ubuntu = "linux-headers-${KERNEL_VERSION}-generic"

# Ubuntu does not have /bin/awk by default!
BINAWK_FILES = "\
    arch/arm/tools/gen-mach-types \
    arch/sh/tools/gen-mach-types \
    arch/x86/tools/distill.awk \
    arch/x86/tools/gen-insn-attr-x86.awk \
    scripts/ver_linux \
    tools/objtool/arch/x86/insn/gen-insn-attr-x86.awk \
    tools/perf/arch/x86/tests/gen-insn-x86-dat.awk \
    tools/perf/util/intel-pt-decoder/gen-insn-attr-x86.awk \
"

do_install_append_ubuntu() {

    cd "$kerneldir/.."
    ln -s "${KERNEL_SOURCE_DIR}" "${KERNEL_HEADERS_DIR}"

    # Ubuntu does not have /bin/awk by default!
    for i in ${BINAWK_FILES}; do
        if [ -f ${D}${KERNEL_SRC_PATH}/$i ]; then
            sed -i s:#!/bin/awk:#!/usr/bin/awk:g ${D}${KERNEL_SRC_PATH}/$i
        fi
    done
}

FILES_${PN}_append_ubuntu = " /usr/src/${KERNEL_HEADERS_DIR}"

# Yocto 3 recipe creates a symlink but apparently does not consider that
# in the FILES statement.
FILES_${PN}_append = " /usr/src/kernel"

INSANE_SKIP_${PN} = "arch"
