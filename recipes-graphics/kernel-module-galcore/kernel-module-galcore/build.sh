#!/bin/bash

########################################################
# establish build environment and build options value
# Please modify the following items according your build environment

# make the script bail on first error (behave like make)
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export KERNEL_DIR=$KERNEL_SRC

export ARCH=${ARCH-arm64}
export ARCH_TYPE=$ARCH

export AQROOT=$DIR
export SDK_DIR=$AQROOT/build/sdk

export CPU_TYPE=cortex-a53
export CPU_ARCH=armv8-a

export CROSS_COMPILE=${CROSS_COMPILE-${TARGET_PREFIX}}
export SOC_PLATFORM=freescale-s32v234

########################################################
# build results will save to $SDK_DIR/
########################################################

if [ "clean" == "$1" ]; then
make --makefile=Kbuild clean
else
make --makefile=Kbuild install
fi
