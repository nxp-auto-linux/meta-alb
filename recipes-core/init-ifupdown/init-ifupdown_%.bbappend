FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
#
# We really should have board specific interfaces files,
# so that the primary Eth connector comes up for dhcp
# by default.
# The search order for bbappend overrides unfortunately
# forces me to use qoriq-ppc rather than the proper
# t4240rdb or t42404rdb-64b.
#
SRC_URI += "file://interfaces"
