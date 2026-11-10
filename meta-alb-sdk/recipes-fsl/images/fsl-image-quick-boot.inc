# Copyright 2024 NXP

IMAGE_INSTALL:remove = "setserial \
    rng-tools \
    udev-extraconf \
    linux-qspi-tool \
"

IMAGE_INSTALL:remove = "${@bb.utils.contains('QB_CONFIG', 'skippcie', 'pciutils', '', d)}"

IMAGE_INSTALL:append = "${@bb.utils.contains('QB_CONFIG', 'systemd-debug', ' systemd-analyze systemd-bootchart', '', d)}"

# Remove syslog from busybox
PACKAGE_EXCLUDE = " busybox-syslog"

autologin() {
    sed -i '/^ExecStart=/s/[^ ]*$/& --autologin root/' ${IMAGE_ROOTFS}${systemd_system_unitdir}/serial-getty@.service
}

ROOTFS_POSTPROCESS_COMMAND:append = " autologin;"
