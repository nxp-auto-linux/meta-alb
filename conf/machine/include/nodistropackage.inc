# To build foreign distro images , we do not want to deploy all Yocto
# packages, but we need their sysroot data for building other packages

# The trick really is that things like PREFERRED_PROVIDER only apply
# to the package generation. The final build step for a rootfs will
# use any package it can find.
# Given that the ubuntu-base files already provide essential packages
# that we must not overwrite, the solution is to not package those.
# We still need to build the recipes though because packages that we
# *do* want to deploy may need their header files etc.

def dnwp_nopackagewrites(d):
	packagelist = (d.getVar('DISTRO_NEVER_WRITE_PACKAGE', True) or '').split()
	pn = (d.getVar('PN', True) or '')
	if pn in packagelist:
		for pkgclass in (d.getVar('PACKAGE_CLASSES') or '').split():
			if pkgclass.startswith('package_'):
				pkgtype = pkgclass.split('_', 1)[1]
				pkgwritefunc = 'do_package_write_%s' % pkgtype
				d.setVarFlag(pkgwritefunc, 'noexec', '1')
				d.setVarFlag('do_package', 'postfuncs', "dnwp_writemanifest")

def dnwp_prefprov(d):
	packagelist = (d.getVar('DISTRO_NEVER_WRITE_PACKAGE', True) or '').split()
	for p in packagelist:
		d.setVar('PREFERRED_PROVIDER_' + p, p)


addhandler distro_never_write_package_pre
distro_never_write_package_pre[eventmask] = "bb.event.ConfigParsed"
python distro_never_write_package_pre () {
	d = e.data

	dnwp_prefprov(d)

	return
}

addhandler distro_never_write_package_post
distro_never_write_package_post[eventmask] = "bb.event.RecipePreFinalise"
python distro_never_write_package_post () {
	d = e.data

	dnwp_nopackagewrites(d)

	return
}

python dnwp_writemanifest() {
    for pkgclass in (d.getVar('PACKAGE_CLASSES') or '').split():
        if pkgclass.startswith('package_'):
            pkgtype = pkgclass.split('_', 1)[1]
            pkgwritetype = 'package_write_%s' % pkgtype
            manifest_path = '%s/manifest-%s-%s.%s' % (d.getVar('SSTATE_MANIFESTS', True), \
d.getVar('SSTATE_MANMACH'), d.getVar('PN', True), pkgwritetype)
            if not os.path.exists(manifest_path):
                open(manifest_path, 'a').close()
}
