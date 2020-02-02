# To build foreign distro images , we do not want to deploy all Yocto
# packages, but we need their sysroot data for building other packages

# The trick really is that things like PREFERRED_PROVIDER only apply
# to the package generation. The final build step for a rootfs will
# use any package it can find unless PACKAGE_EXCLUDE is used, and
# that only works for RPM.
# Given that the ubuntu-base files already provide essential packages
# that we must not overwrite, the solution is to redirect provider use.
# We still need to build the recipes though because packages that we
# *do* want to deploy may need their header files etc.

def dnwp_checkpackagematch(d, pn, patternvar):
    import re
    for pattern in (d.getVar(patternvar) or '').split():
        pnpattern, pkglist, preferredprovider = (pattern.split(':') + 3 * [None])[:3]
        if not pnpattern:
            bb.error("Missing recipe name in nodistropackage")
        if (pn and re.match('^' + pnpattern + '$', pn)):
            if not pkglist:
                pkglist = pnpattern
            pkglist = pkglist.split(',')
            return pnpattern, pkglist, preferredprovider
    return None, None, None

def dnwp_nopackagewrites(d):
    pn = (d.getVar('PN') or '')
    foundnowritepkg, fnwpkglist, fnwprp = dnwp_checkpackagematch(d, pn, 'DISTRO_NEVER_WRITE_PACKAGE')
    foundwritepkg,   fwpkglist,  fwprp  = dnwp_checkpackagematch(d, pn, 'DISTRO_ALWAYS_WRITE_PACKAGE')
    if foundnowritepkg and not foundwritepkg:
        d.appendVar('PACKAGE_EXCLUDE_' + pn, ' ${PACKAGES}')

def dnwp_prefrprov(d):
    # This is really a workaround only, which is limiting the
    # intended flexibility of the mechanism overall. However,
    # I could not find another way to properly set the preferred
    # rprovider. See also dnwp_nopackagewrites() for where this
    # really should happen.
    # I wish I knew how to access all package names at that time
    # but the recipes are not parsed yet ...
    rp = d.getVar('DISTRO_DEFAULT_RPROVIDER')
    dawp = (d.getVar('DISTRO_ALWAYS_WRITE_PACKAGE') or '').split()
    for pattern in (d.getVar('DISTRO_NEVER_WRITE_PACKAGE') or '').split():
        pnpattern, packagelist, preferredrprovider = (pattern.split(':') + 3 * [None])[:3]
        if not pnpattern:
            bb.error("Missing recipe name in nodistropackage")
        preferredrprovider = preferredrprovider or rp
        if preferredrprovider:
            foundalwayswrite = False
            for dawpatten in dawp:
                dawpnpattern, dawpackagelist, dawpreferredrprovider = (dawpattern.split(':') + 3 * [None])[:3]
                # Note that we use pnpattern, i.e., this
                # workaround does not permit us to use real
                # pn names because we can not match against
                # the real recipe names. Hmpf.
                if (pn and re.match("^" + dawpnpattern  + "$", pnpattern)):
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

# This is run after the keys have been expanded, i.e., we can find
# RDEPENDS_<pn> then directly for any name. The idea is that we check
# if any dependency is one that we convert to a different
# PREFERRED_RPROVIDER and fix up the RDEPENDS to avoid file-rdeps
# complaints on packaging.
def dnwp_fixuprdepends(d):
    dp = d.getVar('DISTRO_DEFAULT_RPROVIDER')

    modified = False
    for ppn in (d.getVar('PACKAGES') or '').split():
        rdepkey = 'RDEPENDS_' + ppn
        rpmodified = False
        rdepends = bb.utils.explode_dep_versions2(d.getVar(rdepkey) or '')
        newrdepends = {}
        for rdep in rdepends:
            foundnordeppkg,  fnrpkglist, fnrprp = dnwp_checkpackagematch(d, rdep, 'DISTRO_NEVER_WRITE_PACKAGE')
            foundrdeppkg,    frpkglist,  frprp  = dnwp_checkpackagematch(d, rdep, 'DISTRO_ALWAYS_WRITE_PACKAGE')
            origrdep = rdep
            if foundnordeppkg and not foundrdeppkg:
                fnrprp = fnrprp or dp
                if fnrprp and fnrprp not in newrdepends:
                    rdep = fnrprp
                    rpmodified = True
                    modified = True
            newrdepends[rdep] = rdepends[origrdep]
        if rpmodified:
            d.setVar(rdepkey, bb.utils.join_deps(newrdepends, commasep=False))

            # If we have changed the rdepends, the result is that the build
            # dependency no longer matches the redepends. This means that we
            # need to skip build-dep warnings. This is a bit ugly, but
            # it is a reasonable solution to the QA problem.
            d.appendVar('INSANE_SKIP_' +  ppn, ' build-deps')

python () {
    dnwp_fixuprdepends(d)
    dnwp_nopackagewrites(d)
}
