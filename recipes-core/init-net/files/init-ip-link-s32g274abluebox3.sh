#!/bin/bash


br_link_set()
{
    local sw="$1"
    while [ ! -e "/sys/class/net/$sw" ]; do sleep 0.001; done
    ip link set dev "$sw" master br0
}

echo "Initializing bridge/switch interfaces"

while [ ! -e "/sys/class/net/$sw" ]; do sleep 0.001; done
ip link set dev pfe0 up
ip link add dev br0 type bridge

br_link_set 1ge_p3
br_link_set 1ge_p4

ip link set dev 1ge_p3 up
ip link set dev 1ge_p4 up
ip link set dev br0 up
