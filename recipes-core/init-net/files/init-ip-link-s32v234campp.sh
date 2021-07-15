#!/bin/bash

br_link_set()
{
    local sw="$1"
    while [ ! -e "/sys/class/net/$sw" ]; do sleep 0.001; done
    ip link set dev "$sw" master br0
}

# Network modules need to be brought up only for the primary SoC
# Look for the spi0 node, as it contains the sja nodes
STATUS=`cat /proc/device-tree/soc/aips-bus@40000000/spi@40057000/status 2> /dev/null || true`

if [ "x$STATUS" = "xokay" ]; then
	echo "Initializing bridge/switch interfaces"
	ip link add dev br0 type bridge vlan_filtering 1
	ip link add dev br1 type bridge vlan_filtering 1
	ip link set dev eth0 up
	br_link_set sw0-p0
	br_link_set sw0-p1
	br_link_set sw0-p3
	br_link_set sw0-p4
	br_link_set sw1-p1
	br_link_set sw1-p2
	br_link_set sw1-p3
	br_link_set sw1-p4
	ip link set dev br0 up
	ip link set dev br1 up
	ip link set dev sw0-p0 up
	ip link set dev sw0-p1 up
	ip link set dev sw0-p3 up
	ip link set dev sw0-p4 up
	ip link set dev sw1-p1 up
	ip link set dev sw1-p2 up
	ip link set dev sw1-p3 up
	ip link set dev sw1-p4 up

else
	echo "Not initializing bridge/switch interfaces"
fi
