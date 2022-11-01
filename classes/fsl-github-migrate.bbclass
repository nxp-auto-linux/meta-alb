python __anonymous () {
    string_to_replace = d.getVar("URL_MIGRATEFROM") or ""
    src_uri = d.getVar("SRC_URI") or ""
    if len(string_to_replace) != 0 and string_to_replace in src_uri:
        string_replacement = d.getVar("URL_MIGRATETO") or ""
        src_uri_new = src_uri.replace(string_to_replace, string_replacement)

        # Set the new SRC_URI
        d.setVar("SRC_URI", src_uri_new)

        bb.note("new SRC_URI: '%s'" % src_uri_new)
}
