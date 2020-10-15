# Copyright (C) 2013 Freescale Semiconductor
# Released under the MIT license (see COPYING.MIT for the terms)
# Copyright 2017-2020 NXP

require kernel-module-galcore.inc

URL ?= "git://source.codeaurora.org/external/autobsps32/galcore;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV = "9f423e2942f5093ff51ef4a78f99dd92a915c595"
