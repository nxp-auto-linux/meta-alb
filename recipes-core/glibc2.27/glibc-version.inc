SRCBRANCH ?= "release/2.27/master"
PV = "2.27"
SRCREV_glibc ?= "df3ff4e49d4ee3cbbdaeb0b1cb5dc2344c08be98"
SRCREV_localedef ?= "de5bdbd5e76b5403a4151b9b9f958b6cca36b3e7"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
