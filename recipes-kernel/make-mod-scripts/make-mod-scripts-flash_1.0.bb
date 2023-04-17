inherit varflagsremove

KERNEL_PACKAGE_NAME ?= "kernel-flash"
KERNEL_PACKAGE_NAME_RECIPE ?= "linux-flash"

STAGING_KERNEL_BUILDDIR = "${TMPDIR}/work-shared/${MACHINE}/${KERNEL_PACKAGE_NAME}-build-artifacts"

require recipes-kernel/make-mod-scripts/make-mod-scripts_${PV}.bb

VARFLAGSREMOVE += "do_configure[depends]:remove=virtual/kernel:do_shared_workdir"
VARFLAGSREMOVE += "do_compile[depends]:remove=virtual/kernel:do_compile_kernelmodules"

do_configure[depends] += "${KERNEL_PACKAGE_NAME_RECIPE}:do_shared_workdir"
do_compile[depends] += "${KERNEL_PACKAGE_NAME_RECIPE}:do_compile_kernelmodules"
