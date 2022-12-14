#
# external Linaro glibc specific configuration
#

LIBCEXTENSION = "${@['', '-gnu'][(d.getVar('ABIEXTENSION', True) or '') != '']}"

# Add glibc overrides to the overrides for glibc.
LIBCOVERRIDE = ":libc-glibc"
OVERRIDES .= "${LIBCOVERRIDE}"

PREFERRED_PROVIDER_virtual/libc-locale ?= "external-linaro-toolchain"

PREFERRED_PROVIDER_virtual/nativesdk-libiconv ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/nativesdk-libintl ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/nativesdk-libc ?= "nativesdk-glibc"

LIBC_DEPENDENCIES = "libsegfault \
		     glibc \
		     glibc-dbg \
		     glibc-dev \
		     glibc-utils \
		     glibc-thread-db \
		     ${@get_libc_locales_dependencies(d)}"

LIBC_LOCALE_DEPENDENCIES = "\
	glibc-localedata-i18n \
	glibc-gconv-ibm850 \
	glibc-gconv-cp1252 \
	glibc-gconv-iso8859-1 \
	glibc-gconv-iso8859-15 \
	locale-base-en-us \
	locale-base-en-gb"

def get_libc_locales_dependencies(d):
    if 'libc-locales' in (d.getVar('DISTRO_FEATURES', True) or '').split() :
        return d.getVar('LIBC_LOCALE_DEPENDENCIES', True) or ''
    else:
        return ''


def elt_get_main_version(d):
    version = elt_get_version(d)
    if version != 'UNKNOWN':
        # correctly interpret the Ubuntu toolchain gcc version
        if version.split()[3] == '(Ubuntu/Linaro':
            return version.split()[4].split(')')[0]
        if version.split()[4] == '(Ubuntu/Linaro':
            return version.split()[5].split(')')[0]

        if version.split()[5] == 'GCC':
            return version.split()[6].split(')')[0]
        if version.split()[5] != '(crosstool-NG':
            return version.split()[7].split(')')[0]
        else:
            return version.split()[6].split('-')[3]
    else:
        return version
