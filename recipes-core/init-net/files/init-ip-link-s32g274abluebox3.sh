#!/bin/bash
echo "Initializing bridge/switch interfaces"
ip link set dev pfe0 up
ip link add dev br0 type bridge
ip link set dev 1ge_p3 up
ip link set dev 1ge_p4 up
ip link set dev 1ge_p3 master br0
ip link set dev 1ge_p4 master br0
ip link set dev 1ge_p3 up
ip link set dev 1ge_p4 up
ip link set dev br0 up
