# Yocto unfortunately does not permit override style syntax on
# varflags to remove elements.
# VARFLAGSREMOVE += "<var>[<flag>]:remove=<theflagtoremove>"
# Example:
# VARFLAGSREMOVE += "do_configure[depends]:remove=make-mod-scripts:do_compile"
# <Heinz.Wrobel@nxp.com>

python __anonymous () {
    import re
    p = re.compile(r'(\w+)\[(\w+)\]:remove=(\S+)')
    for element in (d.getVar("VARFLAGSREMOVE") or '').split():
        m = p.match(element)
        var,flag,rmflag = m.group(1,2,3)
        bb.debug(1, 'varflagsremove: ' + var + '[' + flag + ']:remove="' + rmflag + '"')
        vf = d.getVarFlag(var,flag) or ''
        if vf:
           flags = []
           for f in vf.split():
               bb.debug(2, 'checking: "' + f + '"')
               if f != rmflag:
                   flags.append(f)
           vf = " ".join(flags)
           d.setVarFlag(var,flag,vf)
}
