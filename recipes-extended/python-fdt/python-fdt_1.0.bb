# Copyright 2021-2022 NXP
#
# Flattened Device Tree Python Module

SUMMARY = "Flattened Device Tree Python Module"
HOMEPAGE = "https://github.com/molejar/pyFDT"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/molejar/pyFDT.git;branch=master;protocol=https"
SRCREV = "907089ee05addba01f0f671aa8efea3b723934a1"

BBCLASSEXTEND = "native"

S = "${WORKDIR}/git"

inherit setuptools3
RDEPENDS:${PN} += "python3-core python3-stringold"
