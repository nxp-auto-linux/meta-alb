#
# Copyright 2025 NXP
#

require recipes-fsl/images/fsl-image-base.bb
require recipes-fsl/images/fsl-image-core-common.inc
include recipes-fsl/images/fsl-image-s32-common.inc

IMAGE_INSTALL += " \
    iperf3 \
    nano \
    ncftp \
    ptpd \
    pptp-linux \
    phytool \
    tcpdump \
    vim \
"


ROOTFS_POSTPROCESS_COMMAND:append = "set_root_password; disable_ntp_service; remove_pfe_from_network_manager; make_symlinks; "

disable_ntp_service() {
    rm ${IMAGE_ROOTFS}${systemd_system_unitdir}/systemd-timesyncd.service
}

set_root_password() {
    sed -i "s|^root:[^:]*:|root:$(openssl passwd -1 'root'):|" ${IMAGE_ROOTFS}/etc/shadow
}

remove_pfe_from_network_manager() {
    mkdir -p ${IMAGE_ROOTFS}/etc/systemd/network/
    cat <<EOF > ${IMAGE_ROOTFS}/etc/systemd/network/30-wired-pfe.network
[Match]
Name=pfe*
Name=aux*
Name=hif*
 
[Link]
Unmanaged=yes
EOF
}

make_symlinks() {
    ( cd ${IMAGE_ROOTFS} && ln -s /usr/bin/tcpdump usr/sbin/tcpdump )
    mkdir -p ${IMAGE_ROOTFS}/lib/firmware/
    ( cd ${IMAGE_ROOTFS} && ln -sf /tmp/class_s32g.elf    lib/firmware/s32g_pfe_class.fw )
    ( cd ${IMAGE_ROOTFS} && ln -sf /tmp/s32g_pfe_util.fw  lib/firmware/s32g_pfe_util.fw )
    ( cd ${IMAGE_ROOTFS} && ln -sf /tmp/libfci_cli        usr/sbin/libfci_cli )
}