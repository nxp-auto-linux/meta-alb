# Shared helpers for declaring "extra" rootfs partitions on an image.
#
# Extra partitions are declared as varflags on a caller-chosen variable
# (the S32 image classes use the unified ROOTFS_EXTRA_PARTS): the flag
# name is the partition label, and the value is "<pkg>:<filename>".
#
# The recipe <pkg> produces the rootfs artifact <filename>.
# (example: "fsl-image-domu" -> fsl-image-domu:do_image_complete).
#
# These helpers are storage-agnostic so both the UFS and SD card image
# classes can share the same parsing/validation and dependency logic.

# Parse the varflags on <varname> into (label, pkg, filename, ext) tuples.
def parse_image_extra_parts(d, varname):
    flags = d.getVarFlags(varname) or {}

    for label in flags:
        # skip bitbake-internal flags returned by `getVarFlags`
        if label in ('doc', 'vardeps', 'vardepsexclude', 'vardepvalue'):
            continue

        value = d.getVarFlag(varname, label)
        # skip flags with empty value
        if not value:
            continue

        if value.count(':') != 1:
            bb.fatal(f"{varname}[{label}]: invalid value '{value}' "
                     f"(expected exactly one ':' in '<pkg>:<filename>')")

        # split into package and filename
        pkg, _, filename = value.partition(':')
        if not pkg or not filename:
            bb.fatal(f"{varname}[{label}]: invalid value '{value}' "
                     f"(expected '<pkg>:<filename>')")

        # the filename must carry an extension; the image task is derived from it
        base, _, ext = filename.rpartition('.')
        if not base or not ext:
            bb.fatal(f"{varname}[{label}]: filename '{filename}' "
                     f"must be '<name>.<ext>'")

        yield label, pkg, filename, ext



# Build dependencies: "<pkg>:do_image_complete" for every extra partition.
def get_image_extra_parts_deps(d, varname):
    return ' '.join(f'{pkg}:do_image_complete'
                    for _, pkg, _, _ in parse_image_extra_parts(d, varname))


# Flatten the varflags into "<label>:<filename>" entries for the shell.
def get_image_extra_parts(d, varname):
    return ' '.join(f'{label}:{filename}'
                    for label, _, filename, _ in parse_image_extra_parts(d, varname))

