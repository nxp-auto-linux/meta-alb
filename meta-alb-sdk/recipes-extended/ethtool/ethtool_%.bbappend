# Rather than relying on the upstream ethtool version,
# that is changed with each Yocto version, we want to 
# use the same version as the Linux BSP kernel

PV = "6.6"

SRC_URI[sha256sum] = "d4cdb4f7498781fb516e2b3d0eadab28691da3fd3e3445954e82d745de95fb96"

# we remove the ptests in order to avoid the patch application fuzz
# and also because they are not a mandatory part of our process
SRC_URI:remove = "file://run-ptest"
SRC_URI:remove = "file://avoid_parallel_tests.patch"

do_compile_ptest[noexec] = "1"
do_install_ptest[noexec] = "1"
