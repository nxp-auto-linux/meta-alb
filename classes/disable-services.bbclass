rootfs_disable_unnecessary_services() {
    for runlev in 0 5 6; do
        rm -fr ${IMAGE_ROOTFS}/etc/rc${runlev}.d/*sshd
        rm -fr ${IMAGE_ROOTFS}/etc/rc${runlev}.d/*hwclock.sh
        rm -fr ${IMAGE_ROOTFS}/etc/rc${runlev}.d/*netperf
    done
}
