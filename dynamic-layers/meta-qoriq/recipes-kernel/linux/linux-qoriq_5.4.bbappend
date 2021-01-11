FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://0001-perf-tests-bp_account-Make-global-variable-static.patch \
    file://0001-perf-cs-etm-Move-definition-of-traceid_list-global-v.patch \
    file://0001-perf-bench-Share-some-global-variables-to-fix-build-.patch \
    file://0001-libtraceevent-Fix-build-with-binutils-2.35.patch \
"
