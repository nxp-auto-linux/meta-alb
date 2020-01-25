# To build foreign distro images , we do not want to deploy all Yocto
# packages, but we need their sysroot data for building other packages

# The trick really is that things like PREFERRED_PROVIDER only apply
# to the package generation. The final build step for a rootfs will
# use any package it can find.
# Given that the ubuntu-base files already provide essential packages
# that we must not overwrite, the solution is to not package those.
# We still need to build the recipes though because packages that we
# *do* want to deploy may need their header files etc.

def dnwp_checkpackagematch(d, pn, patternvar):
    import re
    for pattern in (d.getVar(patternvar) or '').split():
        pnpattern, pkglist, preferredprovider = (pattern.split(':') + 3 * [None])[:3]
        if not pnpattern:
            bb.error("Missing recipe name in nodistropackage")
        pnpattern  = "^" + pnpattern  + "$"
        if (pn and re.match(pnpattern, pn)):
            if not pkglist:
                pkglist = pnpattern
            pkglist = pkglist.split(',')
            return pnpattern, pkglist, preferredprovider
    return None, None, None

def dnwp_writemanifest(d):
    # This function is required for Yocto >=sumo because Yocto will look
    # at manifest files to figure out dependencies. If we do not write
    # a package, nothing will show up in the sstate, and no manifest
    # will be there. So we need to create a dummy manifest for any
    # recipe that we do not write packages for.
    pn = d.getVar('PN')
    for pkgclass in (d.getVar('PACKAGE_CLASSES') or '').split():
        if pkgclass.startswith('package_'):
            pkgtype = pkgclass.split('_', 1)[1]
            pkgwritetype = 'package_write_%s' % pkgtype
            ssm = d.getVar('SSTATE_MANIFESTS', True)
            manifest_path = d.expand("${SSTATE_MANIFESTS}/manifest-${SSTATE_MANMACH}-${PN}.%s" % pkgwritetype)
            if not os.path.exists(ssm):
                bb.utils.mkdirhier(ssm)
            if not os.path.exists(manifest_path):
                open(manifest_path, 'a').close()

def dnwp_nopackagewrites(d):
    pn = (d.getVar('PN') or '')
    foundnowritepkg, fnwpkglist, fnwprp = dnwp_checkpackagematch(d, pn, 'DISTRO_NEVER_WRITE_PACKAGE')
    foundwritepkg,   fwpkglist,  fwprp  = dnwp_checkpackagematch(d, pn, 'DISTRO_ALWAYS_WRITE_PACKAGE')
    if foundnowritepkg and not foundwritepkg:
        fnwprp = fnwprp or d.getVar('DISTRO_DEFAULT_RPROVIDER')
        if fnwprp:
            # The following line does not work because we
            # are operating on the task local datastore and
            # not the global data store that is evaluated
            # after parsing to resolve all the dependencies.
            # The update should happen here, but I have not
            # found a reliable way to properly access the
            # correct datastore copy.
            # We should be able to correctly update it if
            # we could access it, because the event
            # RecipePreFinalise is processed before any
            # preferred rproviders are evaluated.
            # See dnwp_prefrprov() for a workaround.
            #d.setVar('PREFERRED_RPROVIDER_' + pn, fnwprp)
            pass
        for pkgclass in (d.getVar('PACKAGE_CLASSES') or '').split():
            if pkgclass.startswith('package_'):
                # With the following lines we prohibit
                # package deployment into the repository.
                # By doing that, we prohibit use of the
                # package in any target image.
                pkgtype = pkgclass.split('_', 1)[1]
                d.setVarFlag('do_package_write_%s' % pkgtype, 'noexec', '1')
                d.setVarFlag('do_package_write_%s_setscene' % pkgtype, 'noexec', '1')
        d.setVarFlag('do_package' , 'noexec', '1')
        d.setVarFlag('do_package_setscene' , 'noexec', '1')
        d.setVarFlag('do_packagedata' , 'noexec', '1')
        d.setVarFlag('do_packagedata_setscene' , 'noexec', '1')
        d.setVarFlag('do_package_qa' , 'noexec', '1')
        d.setVarFlag('do_package_qa_setscene' , 'noexec', '1')
        # As we won't write the package anyway, we may want to write
        # the empty manifest now at parsing time.
        dnwp_writemanifest(d)

def dnwp_prefrprov(d):
    # This is really a workaround only, which is limiting the
    # intended flexibility of the mechanism overall. However,
    # I could not find another way to properly set the preferred
    # rprovider. See also dnwp_nopackagewrites() for where this
    # really should happen.
    # I wish I knew how to access all package names at that time
    # but the recipes are not parsed yet ...
    dawp = (d.getVar('DISTRO_ALWAYS_WRITE_PACKAGE') or '').split()
    for pattern in (d.getVar('DISTRO_NEVER_WRITE_PACKAGE') or '').split():
        pnpattern, packagelist, preferredrprovider = (pattern.split(':') + 3 * [None])[:3]
        if not pnpattern:
            bb.error("Missing recipe name in nodistropackage")
        preferredrprovider = preferredrprovider or d.getVar('DISTRO_DEFAULT_RPROVIDER')
        if preferredrprovider:
            foundalwayswrite = False
            for dawpatten in dawp:
                dawpnpattern, dawpackagelist, dawpreferredrprovider = (dawpattern.split(':') + 3 * [None])[:3]
                # Note that we use pnpattern, i.e., this
                # workaround does not permit us to use real
                # pn names because we can not match against
                # the real recipe names. Hmpf.
                dawpnpattern  = "^" + dawpnpattern  + "$"
                if (pn and re.match(dawpnpattern, pnpattern)):
                    foundalwayswrite = True
                    break

            if not foundalwayswrite:
                if not packagelist:
                    packagelist = pnpattern
                packagelist = packagelist.split(',')
                for p in packagelist:
                    d.setVar('PREFERRED_RPROVIDER_' + p, preferredrprovider)

addhandler distro_never_write_package_pre
distro_never_write_package_pre[eventmask] = "bb.event.ConfigParsed"
python distro_never_write_package_pre () {
    dnwp_prefrprov(d)
}

addhandler distro_never_write_package_post
distro_never_write_package_post[eventmask] = "bb.event.RecipePreFinalise"
python distro_never_write_package_post () {
    dnwp_nopackagewrites(d)
}

