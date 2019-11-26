# poky (sumo) hardcode the preferred vesion for openssl to 1.0.%,
# in meta/conf/distro/include/default-versions.inc
#     PREFERRED_VERSION_openssl = "1.0.%"
# The assignment can not be overrided in meta-alb files, as it is "=" instead
# of "?="
# To fix the poky hardcoding, this file set the variable to the desired version


OPENSSLVERSION ?= "1.1.1c"

def openssl_version(d):
	newversion = d.getVar('OPENSSLVERSION')
	d.setVar('PREFERRED_VERSION_openssl', newversion)
	d.setVar('PREFERRED_VERSION_openssl-native', newversion)
	d.setVar('PREFERRED_VERSION_nativesdk-openssl', newversion)



addhandler openssl_version_handler
openssl_version_handler[eventmask] = "bb.event.ConfigParsed"
python openssl_version_handler () {
	openssl_version(d)
}

