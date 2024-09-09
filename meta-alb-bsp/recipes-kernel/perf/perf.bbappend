EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"

PACKAGECONFIG += "libtraceevent"
PACKAGECONFIG[libtraceevent] = ",NO_LIBTRACEEVENT=1,libtraceevent"

RDEPENDS:${PN}-tests += "perl"

# The architectures that need this file can be found in
#    tools/include/uapi/asm/bpf_perf_event.h
# We are only listing supported arches at the moment
PERF_BPF_EVENT_SRC ?= '${@bb.utils.contains_any("ARCH", [ "riscv", "arm64" ], "arch/${ARCH}/include/uapi/asm/bpf_perf_event.h", "", d)}'

PERF_SRC:append = " \
	arch/arm64/tools \
	${PERF_BPF_EVENT_SRC} \
"

do_configure:prepend() {
	sed -i 's,PKG_CONFIG = $(CROSS_COMPILE)pkg-config,#PKG_CONFIG,' ${S}/tools/perf/Makefile.perf
}
