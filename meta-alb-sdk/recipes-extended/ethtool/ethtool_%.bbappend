# Rather than relying on the upstream ethtool version,
# that is changed with each Yocto version, we want to 
# use the same version as the Linux BSP kernel

PV = "6.11"

SRC_URI[sha256sum] = "a8bee475d991939cad182b7dbcf3a1f139aeed8cb0add73d72084cd71d43282b"

# we remove the ptests in order to avoid the patch application fuzz
# and also because they are not a mandatory part of our process
SRC_URI:remove = "file://run-ptest"
SRC_URI:remove = "file://avoid_parallel_tests.patch"

do_compile_ptest[noexec] = "1"
do_install_ptest[noexec] = "1"
