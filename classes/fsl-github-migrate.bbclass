# This is a kirkstone specific default list of variables in various
# recipes of meta-freescale/meta-qoriq that can benefit from
# our hacky URL translation. First we list generics, then qoriq, then
# imx. If layers are changed for a BSP, everything needs to be
# reviewed. This is HACKY stuff.
URL_MIGRATEVARS ?= "\
    SRC_URI \
\
    OPTEE_TEST_SRC \
    OPTEE_OS_SRC \
    OPTEE_CLIENT_SRC \
\
    GST1.0_SRC \
    GST1.0-PLUGINS-BASE_SRC \
    GST1.0-PLUGINS-BAD_SRC \
    GST1.0-PLUGINS-GOOD_SRC \
    OPENCV_SRC \
    IMX_LIBDRM_SRC \
    MRVL_SRC \
    KERNEL_SRC \
    ISP_KERNEL_SRC \
    KEYCTL_CAAM_SRC \
    WAYLAND_PROTOCOLS_SRC \
    WESTON_SRC_CAF \
"
URL_MIGRATELIST ?= ""

# We translate prior to key expansion or we may miss content.
# To do that we cannot use an anonymous function. We need to hook
# into the event that happens before expansion
addhandler migrate_eventhandler
migrate_eventhandler[eventmask] = "bb.event.RecipePreFinalise"
python migrate_eventhandler () {
    if isinstance(e, bb.event.RecipePreFinalise):
        migrationlist = (d.getVar("URL_MIGRATELIST") or "").split()
        vars = d.getVar("URL_MIGRATEVARS").split()
        for var in vars:
            src_uri = d.getVar(var) or ""
            for entry in migrationlist:
                string_to_replace  = (entry.split(":") + 2 * [None])[0] or ""
                string_replacement = (entry.split(":") + 2 * [None])[1] or ""
                if len(string_to_replace) != 0 and string_to_replace in src_uri:
                    src_uri_new = src_uri.replace(string_to_replace, string_replacement)

                    d.setVar(var, src_uri_new)

                    bb.debug(1, "old %s: '%s'" % (var, src_uri))
                    bb.debug(1, "new %s: '%s'" % (var, src_uri_new))

                    src_uri = src_uri_new
}
