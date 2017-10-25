SUMMARY = "required pkgs for LTP"
LICENSE = "MIT"

inherit packagegroup

RRECOMMENDS_packagegroup-fsl-ltp = "\
    acl \
    at \
    bind-utils \ 
    binutils \
    canutils \
    dhcp-client \
    dhcp-server \
    dnsmasq \
    expect \
    inetutils \
    libaio \
    netkit-rpc \
    netkit-rusers-client \
    netkit-rusers-server \
    netkit-rwho-client \
    netkit-rwho-server \
    numactl \
    quota \
"
