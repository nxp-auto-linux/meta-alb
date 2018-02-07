# Copyright (C) 2012-2015 Freescale Semiconductor
# Copyright (C) 2012-2013 O.S. Systems Software LTDA.
# Released under the MIT license (see COPYING.MIT for the terms)
# Copyright 2017-2018 NXP

DESCRIPTION = "GPU driver and apps for s32v2xx"
SECTION = "libs"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://usr/include/gc_vdk.h;endline=26;md5=e4ea72ff91665efb91435921a29148b7"

DEPENDS += "kernel-module-galcore"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

PE = "1"
