# Copyright (C) 2013 Freescale Semiconductor
# Released under the MIT license (see COPYING.MIT for the terms)
# Copyright 2017-2020 NXP

require kernel-module-galcore.inc
URL ?= "git://source.codeaurora.org/external/autobsps32/galcore;protocol=https"
BRANCH ?= "${RELEASE_BASE}-${PV}"
SRC_URI = "${URL};branch=${BRANCH}"
SRCREV = "7eed97bdbd065a8990c3d6b526e0d63d383d9871"
