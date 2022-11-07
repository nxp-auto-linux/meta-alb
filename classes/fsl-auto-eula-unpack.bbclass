
inherit fsl-eula-unpack

# fsl-auto-eula-unpack.bbclass patches fsl-eula-unpack.bbclass.
#
# fsl-eula-unpack.bbclass provides the mechanism used for unpacking
# the .bin file downloaded by HTTP and handle the EULA acceptance.
#
# To use it, the 'fsl-eula' parameter needs to be added to the
# SRC_URI entry, e.g:
#
#  SRC_URI = "${FSL_MIRROR}/firmware-imx-${PV};fsl-eula=true"
#
# The purpose of fsl-auto-eula-unpack.bbclass is to override function 
# fsl_bin_do_unpack() from fsl-eula-unpack.bbclass, which uses
# url.localpath which is None for rocko and python 3.x.
# Workaround is to use url.path instead of url.localpath.

LIC_FILES_CHKSUM:remove = " file://${FSL_EULA_FILE};md5=d4f548f93b5fe0ee2bc86758c344412d"
LIC_FILES_CHKSUM:append = " file://${FSL_EULA_FILE};md5=ab61cab9599935bfe9f700405ef00f28"

python fsl_bin_do_unpack() {
    src_uri = (d.getVar('SRC_URI', True) or "").split()

    if len(src_uri) == 0:
        return

    localdata = bb.data.createCopy(d)
    bb.data.update_data(localdata)

    rootdir = localdata.getVar('WORKDIR', True)
    fetcher = bb.fetch2.Fetch(src_uri, localdata)

    for url in fetcher.ud.values():
        save_cwd = os.getcwd()
        # Check for supported fetchers
        if url.type in ['http', 'https', 'ftp', 'file']:
            if url.parm.get('fsl-eula', False):
                if not url.localpath:
                    url.localpath = url.basepath
                # If download has failed, do nothing
                if not os.path.exists(url.localpath):
                    bb.debug(1, "Exiting as '%s' cannot be found" % url.basename)
                    return

                # Change to the working directory
                bb.note("Handling file '%s' as a Freescale's EULA binary." % url.basename)
                save_cwd = os.getcwd()
                os.chdir(rootdir)

                cmd = "sh %s --auto-accept --force" % (url.localpath)
                bb.fetch2.runfetchcmd(cmd, d, quiet=True)

        # Return to the previous directory
        os.chdir(save_cwd)
}

