FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
	file://0001-linux-qoriq-arm64-kernel-now-conserves-power-on-stop.patch \
	file://0002-kernel-LS2-RDB-device-tree-was-not-quite-correct.patch \
	file://0001-fsl-mc-DPAA2-interfaces-are-in-U-Boot-order-now.patch \
	file://0001-drivers-net-aquantia-Patched-up-AQR405-rate-support.patch \
	file://0001-linux-qoriq-Fix-LX2160A-DTS-for-full-PL011-UART.patch \
	file://0001-fsl-lx2160a.dtsi-Added-thermal-zone-name.patch \
	file://0002-linux-qoriq-Backport-of-AQR113-C-support-from-5.15.8.patch \
"
