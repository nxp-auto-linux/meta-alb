#!/bin/sh
### BEGIN INIT INFO
# Provides:          sja1105pqrs
# Required-Start:    $remote_fs $syslog
# Required-Stop:     $remote_fs $syslog
# Default-Start:     3 5
# Default-Stop:
# Short-Description: Loading sja1105pqrs module
# Description:       Inserts SJA1105PQRS kernel module. This enables ethernet.
### END INIT INFO
# Copyright 2018 NXP


OPT=""
lsmod | grep -q "^sja1105pqrs"  > /dev/null || insmod /lib/modules/`uname -r`/kernel/drivers/spi/sja1105pqrs.ko ${OPT}
