def set_insane_skip(d):
	packagelist = (d.getVar('INSANE_SKIP_LIST', True) or '').split()
	for p in packagelist:
		crt = d.getVar('INSANE_SKIP_' + p, True)
		if not crt:
			crt = " "
		d.setVar('INSANE_SKIP_' + p, crt + INSANE_SKIP_VALUE)


addhandler set_insane_skip_handler
set_insane_skip_handler[eventmask] = "bb.event.ConfigParsed"
python set_insane_skip_handler () {
	d = e.data

	set_insane_skip(d)

	return
}

