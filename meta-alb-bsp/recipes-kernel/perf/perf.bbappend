EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"

PACKAGECONFIG += "libtraceevent"
PACKAGECONFIG[libtraceevent] = ",NO_LIBTRACEEVENT=1,libtraceevent"

PERF_SRC:append = " \
	arch/arm64/tools \
"

do_configure:prepend() {
	sed -i 's,PKG_CONFIG = $(CROSS_COMPILE)pkg-config,#PKG_CONFIG,' ${S}/tools/perf/Makefile.perf
}
