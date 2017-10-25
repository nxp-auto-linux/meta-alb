# patch securetty file to include both of LF0 and LF1 serial port for S32V234

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://addserialsecuretty.patch;pnum=1"
