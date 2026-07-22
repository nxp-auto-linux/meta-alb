#
# Copyright 2026 NXP
#
# Remove runtime dependencies on kernel packages from selected packages.
#
# Packages whose names match one of the prefixes listed in
# REMOVE_KERNEL_DEPS_PREFIXES will have dependencies on kernel-image and
# versioned kernel packages removed from their RDEPENDS and RRECOMMENDS.
#
# This class is intended for packages such as kernel modules that must not
# automatically pull kernel packages into the final image through generated
# runtime dependencies.
#
# Usage:
#   inherit remove-kernel-runtime-deps
#   REMOVE_KERNEL_DEPS_PREFIXES = "kernel-module-ipc-shm"
#
# The filtering is performed during package split processing.
#

python remove_kernel_runtime_deps() {
    prefixes = (d.getVar("REMOVE_KERNEL_DEPS_PREFIXES") or "").split()
    kernel_pkg = d.getVar("KERNEL_PACKAGE_NAME") or "kernel"
    kernel_prefix = kernel_pkg + "-"

    if not prefixes:
        return

    def is_kernel_dep(dep):
        suffix = dep[len(kernel_prefix):]

        return (
            dep.startswith(kernel_pkg + "-image") or
            (dep.startswith(kernel_prefix) and suffix[:1].isdigit())
        )

    def remove_kernel_deps(var):
        deps = bb.utils.explode_dep_versions2(d.getVar(var) or "")
        if any(is_kernel_dep(k) for k in deps):
            d.setVar(var, bb.utils.join_deps(
                {k: v for k, v in deps.items() if not is_kernel_dep(k)},
                commasep=False
            ))

    names = set()
    for pkg in (d.getVar("PACKAGES") or "").split():
        names |= {pkg, d.getVar("PKG:%s" % pkg) or pkg}

    for name in names:
        if any(name.startswith(prefix) for prefix in prefixes):
            remove_kernel_deps("RDEPENDS:%s" % name)
            remove_kernel_deps("RRECOMMENDS:%s" % name)
}

PACKAGESPLITFUNCS:append = " remove_kernel_runtime_deps"