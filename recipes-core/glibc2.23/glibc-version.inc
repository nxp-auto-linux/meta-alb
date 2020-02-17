SRCBRANCH ?= "release/2.23/master"
PV = "2.23"
SRCREV_glibc ?= "e742928c1592b43db6809db4f39e67be151cdd27"
SRCREV_localedef ?= "5a81ff9f06a7a808d4c3d37bbf34077a4c5902ed"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
