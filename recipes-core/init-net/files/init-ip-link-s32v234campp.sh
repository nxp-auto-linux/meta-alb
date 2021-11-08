#!/bin/bash

# Network modules need to be brought up only for the primary SoC
# Look for the spi0 node, as it contains the sja nodes
STATUS=`cat /proc/device-tree/soc/aips-bus@40000000/spi@40057000/status 2> /dev/null || true`

if [ "x$STATUS" = "xokay" ]; then
	echo "Initializing bridge/switch interfaces"
	ip link add dev br0 type bridge vlan_filtering 1
	ip link set dev eth0 up
	ip link set dev sw0-p1 master br0
	ip link set dev sw0-p3 master br0
	ip link set dev sw0-p4 master br0
	ip link set dev sw1-p1 master br0
	ip link set dev sw1-p2 master br0
	ip link set dev sw1-p3 master br0
	ip link set dev br0 up
	ip link set dev sw0-p1 up
	ip link set dev sw0-p3 up
	ip link set dev sw0-p4 up
	ip link set dev sw1-p1 up
	ip link set dev sw1-p2 up
	ip link set dev sw1-p3 up
	# In initramfs vlan filtering is not enabled by ip tool even if that
	# is explicitly specified. So we have to enable it from sysfs.
	echo 1 > /sys/devices/virtual/net/br0/bridge/vlan_filtering

else
	echo "Not initializing bridge/switch interfaces"
fi
