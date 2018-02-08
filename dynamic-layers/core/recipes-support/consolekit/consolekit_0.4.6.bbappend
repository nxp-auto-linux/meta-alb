#
# We want pam+polkit support in consolekit for the full featured image.
# Without it, the XFCE desktop will time out when trying to talk
# to ConsoleKit.Manager
# To me, this seems to indicate that policykit really should be a DISTRO_FEATURE
# within Yocto, but it isn't as it seems.
#
# <Heinz.Wrobel@nxp.com>
#
PACKAGECONFIG_append = " policykit"

#
# This is a workaround for a bug in poky/meta/classes/package.bbclass
# The class does not treat directory symlinks properly, leading to
# directories created in split packages, when symlinks should have
# been created. That causes a mismatch between package files and spec
# file that an rpm.org rpm version 4 rpmbuild will trip over.
#
# The workaround for the problem is to ensure the symlink is
# referenced in FILES before any element beneath the symlink is
# referenced. We also need to make sure that the real path exists
# before that happens and that we do not have duplicate file references,
# but that is already covered in the existing recipe.
#
##FILES_${PN}_prepend = "${localstatedir}/volatile/log ${localstatedir}/log "

