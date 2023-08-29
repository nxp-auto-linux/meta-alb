#!/bin/sh
# -*- mode: shell-script; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
#
# Copyright (C) 2012, 2013 O.S. Systems Software LTDA.
# Authored-by:  Otavio Salvador <otavio@ossystems.com.br>
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
# Add options for the script
# Copyright (C) 2013-2015 Freescale Semiconductor, Inc.
# Copyright 2016-2018, 2020 NXP
#  Modifications by:
#          Heinz Wrobel <heinz.wrobel@nxp.com>
#          Ionut Vicovan <ionut.vicovan@nxp.com>

# Use hardcoded name of the script here, do not attempt to determine it.
# It is the safest way, since we want to support all shells
# and practice showed that there is no simple generic way to get the
# script name when sourced (we can't rely on using built-in variables
# $0, $_, $BASH_SOURCE since they behave differently or are not always available)
PROGNAME="nxp-setup-alb.sh"

# This defines which machine conf files we ignore for the underlying SDK
MACHINEEXCLUSION="^imx|^twr"
# Which machine types are ARM based and need the linaro toolchain?
# This should be done properly by checking the conf files, really
ARMMACHINE="^ls|^s32|^lx"


DEFAULT_DISTRO="fsl-auto"
COMPANY="NXP"

# Any bluebox or LS machine type
BBLSMACHINE=".+bbmini|.+bluebox.+|ls.+|lx.+"

# Any Ubuntu machine type
UBUNTUMACHINE=".+ubuntu"

# Supported yocto version
YOCTOVERSION="kirkstone"

# Error codes
EINVAL=128

if [ -z "$ZSH_NAME" ] && echo "$0" | grep -q "$PROGNAME"; then
    echo "ERROR: This script needs to be sourced."
    SCRIPT_PATH=`readlink -f $0`
    if [ "`readlink $SHELL`" = "dash" ];then
        echo "Try run command \"set -- -h; . $SCRIPT_PATH\" to get help."
    else
        echo "Try run command \". $SCRIPT_PATH -h\" to get help."
    fi
    unset SCRIPT_PATH PROGNAME
    exit
else
    if [ -n "$BASH_SOURCE" ]; then
        ROOTDIR="`readlink -f $BASH_SOURCE | xargs dirname`"
    elif [ -n "$ZSH_NAME" ]; then
        ROOTDIR="`readlink -f $0 | xargs dirname`"
    else
        ROOTDIR="`readlink -f $PWD | xargs dirname`"
    fi
    if ! [ -e "$ROOTDIR/$PROGNAME" ];then
        echo "Go to where $PROGNAME locates, then run: . $PROGNAME <args>"
        unset ROOTDIR PROGNAME
        return
    fi
fi

SOURCESDIR="sources"
ALBROOTDIR=${ROOTDIR}/${SOURCESDIR}/meta-alb

# Validate by size that this file is (the same as) the original one in meta-alb
ORIGINALFILE="`find \"$ALBROOTDIR\" -name $PROGNAME`"
if [ -e "$ORIGINALFILE" ]; then

    PROGSIZE=`ls -l "$ROOTDIR/$PROGNAME" | cut -d' ' -f 5`
    ORIGINALSIZE=`ls -l "$ORIGINALFILE" | cut -d' ' -f 5`

    if [ "$PROGSIZE" -ne "$ORIGINALSIZE" ]; then
        echo "Found original script: \"$ORIGINALFILE\""
        echo "WARNING: original script \"$ORIGINALFILE\" is different from the script you are running"
        echo "Please update this script (\"$ROOTDIR/$PROGNAME\") or source directly \"$ORIGINALFILE\""
        return
    fi
fi

# Check if current user is root
if [ "$(whoami)" = "root" ]; then
    echo "ERROR: Do not use the BSP as root. Exiting..."
    unset ROOTDIR PROGNAME
    return
fi

OEROOTDIR=${ROOTDIR}/${SOURCESDIR}/poky
if [ -e ${ROOTDIR}/${SOURCESDIR}/oe-core ]; then
    OEROOTDIR=${ROOTDIR}/${SOURCESDIR}/oe-core
fi
FSLROOTDIR=${ROOTDIR}/${SOURCESDIR}/meta-freescale
PROJECT_DIR=${ROOTDIR}/build_${MACHINE}

prompt_message () {
local i=''
echo "Welcome to ${COMPANY} Auto Linux BSP (Reference Distro)

The Yocto Project has extensive documentation about OE including a
reference manual which can be found at:
    http://yoctoproject.org/documentation

For more information about OpenEmbedded see their website:
    http://www.openembedded.org/

You can now run 'bitbake <target>'
"
    echo "Targets specific to ${COMPANY}:"
    for layer in $(echo $LAYER_LIST | xargs); do
        fsl_recipes=$(find ${ROOTDIR}/${SOURCESDIR}/$layer -path "*recipes-*/images/fsl*.bb" -or -path "images/fsl*.bb" 2> /dev/null)
        if [ -n "$fsl_recipes" ]
        then
            for i in $(echo $fsl_recipes | xargs);do
                i=$(basename $i);
                i=$(echo $i | sed -e 's,^\(.*\)\.bb,\1,' 2> /dev/null)
                echo "    $i";
            done
        fi
    done

    echo "To return to this build environment later please run:"
    echo "    . $PROJECT_DIR/SOURCE_THIS"
}

clean_up()
{
   unset PROGNAME ROOTDIR OEROOTDIR FSLROOTDIR PROJECT_DIR \
         EULA EULA_FILE LAYER_LIST MACHINE FSLDISTRO \
         OLD_OPTIND CPUS JOBS THREADS DOWNLOADS CACHES DISTRO \
         setup_flag setup_h setup_j setup_t setup_l setup_builddir \
         setup_download setup_sstate setup_error layer append_layer \
         extra_layers distro_override MACHINELAYER MACHINEEXCLUSION \
         ARMMACHINE

   unset -f usage prompt_message
}

usage() {
    echo "Usage: . $PROGNAME -m <machine>"
    ls $FSLROOTDIR/conf/machine/*.conf > /dev/null 2>&1

    if [ $? -eq 0 ]; then
        echo -n -e "\n    Supported machines: "
        for layer in $(eval echo $USAGE_LIST); do
            if [ -d ${ROOTDIR}/${SOURCESDIR}/${layer}/conf/machine ]; then
                echo -n -e "`ls ${ROOTDIR}/${SOURCESDIR}/${layer}/conf/machine | grep "\.conf" \
                   | egrep -v "^${MACHINEEXCLUSION}" | sed s/\.conf//g | xargs echo` "
            fi
        done
        echo ""
    else
        echo "    ERROR: no available machine conf file is found. "
    fi

    echo "    Optional parameters:
    * [-m machine]: the target machine to be built.
    * [-b path]:    non-default path of project build folder.
    * [-e layers]:  extra layer names
    * [-D distro]:  override the default distro selection ($DEFAULT_DISTRO)
    * [-j jobs]:    number of jobs for make to spawn during the compilation stage.
    * [-t tasks]:   number of BitBake tasks that can be issued in parallel.
    * [-d path]:    non-default path of DL_DIR (downloaded source)
    * [-c path]:    non-default path of SSTATE_DIR (shared state Cache)
    * [-l]:         lite mode. To help conserve disk space, deletes the building
                    directory once the package is built.
    * [-h]:         help
"
    if [ "`readlink $SHELL`" = "dash" ];then
        echo "
    You are using dash which does not pass args when being sourced.
    To workaround this limitation, use \"set -- args\" prior to
    sourcing this script. For exmaple:
        \$ set -- -m s32g274ardb2 -j 3 -t 2
        \$ . $ROOTDIR/$PROGNAME
"
    fi
}


add_layers_for_machines()
{
    # add the layer specified in PARAM_LAYER_LIST only for the machines
    # contained in PARAM_MACHINE_LIST

    PARAM_LAYER_LIST=$1
    PARAM_MACHINE_LIST=$2

    echo ${MACHINE} | egrep -q "${PARAM_MACHINE_LIST}"
    if [ $? -eq 0 ]; then
        for layer in $(eval echo ${PARAM_LAYER_LIST}); do
            if [ -e "${ROOTDIR}/${SOURCESDIR}/${layer}" ]; then
                LAYER_LIST="$LAYER_LIST \
                    $layer \
                "
            fi
        done
    fi
}

is_not_ubuntu_machine()
{
    echo ${MACHINE} | egrep -q "${UBUNTUMACHINE}"
    return $?
}

# parse the parameters
OLD_OPTIND=$OPTIND
while getopts "m:j:t:b:d:e:D:c:lh" setup_flag
do
    case $setup_flag in
        m) MACHINE="$OPTARG";
           ;;
        j) setup_j="$OPTARG";
           ;;
        t) setup_t="$OPTARG";
           ;;
        b) setup_builddir="$OPTARG";
           ;;
        d) setup_download="$OPTARG";
           ;;
        e) extra_layers="$OPTARG";
           ;;
        D) distro_override="$OPTARG";
           ;;
        c) setup_sstate="$OPTARG";
           ;;
        l) setup_l='true';
           ;;
        h) setup_h='true';
           ;;
        ?) setup_error='true';
           ;;
    esac
done
OPTIND=$OLD_OPTIND

ALB_LAYER_LIST=" \
    meta-alb \
    meta-alb-dev \
    meta-aa-integration \
    meta-vnp \
    meta-gvip \
    \
    $extra_layers \
"

LAYER_LIST=" \
    meta-openembedded/meta-oe \
    meta-openembedded/meta-multimedia \
    meta-openembedded/meta-python \
    meta-openembedded/meta-python2 \
    meta-openembedded/meta-networking \
    meta-openembedded/meta-gnome \
    meta-openembedded/meta-filesystems \
    meta-openembedded/meta-webserver \
    meta-openembedded/meta-perl \
    meta-openembedded/meta-xfce \
    meta-virtualization \
    meta-security \
    \
    meta-freescale \
"

LSLAYERS=" \
    meta-qoriq \
"

USAGE_LIST="$LAYER_LIST \
	$ALB_LAYER_LIST \
	$LSLAYERS \
"

# Really, conf files should be checked and not the machine name ...
echo ${MACHINE} | egrep -q "${ARMMACHINE}"
if [ $? -eq 0 ]; then
    add_layers_for_machines "${LSLAYERS}" "${BBLSMACHINE}"

    # ALB layers after LSLAYERS, to make sure ALB .bbappends are applied last
    LAYER_LIST="$LAYER_LIST \
    $ALB_LAYER_LIST \
    "
fi
 

EULA_FILE="$ALBROOTDIR/EULA"

# check the "-h" and other not supported options
if test $setup_error || test $setup_h; then
    usage && clean_up && return
fi


unset DISTRO
if [ -n "$distro_override" ]; then
    DISTRO="$distro_override";
fi

if [ -z "$DISTRO" ]; then
    DISTRO="$DEFAULT_DISTRO"
fi

# Check the machine type specified
# Note that we intentionally do not test ${MACHINEEXCLUSION}
unset MACHINELAYER
if [ -n "${MACHINE}" ]; then
    for layer in $(eval echo $LAYER_LIST); do
        if [ -e ${ROOTDIR}/${SOURCESDIR}/${layer}/conf/machine/${MACHINE}.conf ]; then
            MACHINELAYER="${ROOTDIR}/${SOURCESDIR}/${layer}"
            break
        fi
    done
else
    usage && clean_up && return $EINVAL
fi

if [ -n "${MACHINELAYER}" ]; then 
    echo "Configuring for ${MACHINE} and distro ${DISTRO}..."
else
    echo -e "\nThe \$MACHINE you have specified ($MACHINE) is not supported by this build setup."
    usage && clean_up && return $EINVAL
fi

# set default jobs and threads
CPUS=`grep -c processor /proc/cpuinfo`
JOBS="$(( ${CPUS} * 3 / 2))"
THREADS="$(( ${CPUS} * 2 ))"

# check optional jobs and threads
if echo "$setup_j" | egrep -q "^[0-9]+$"; then
    JOBS=$setup_j
fi
if echo "$setup_t" | egrep -q "^[0-9]+$"; then
    THREADS=$setup_t
fi

# set project folder location and name
if [ -n "$setup_builddir" ]; then
    if echo $setup_builddir |grep -q ^/;then
        PROJECT_DIR="${setup_builddir}"
    else
        PROJECT_DIR="`pwd`/${setup_builddir}"
    fi
else
    PROJECT_DIR=${ROOTDIR}/build_${MACHINE}
fi
mkdir -p $PROJECT_DIR

if [ -n "$setup_download" ]; then
    if echo $setup_download |grep -q ^/;then
        DOWNLOADS="${setup_download}"
    else
        DOWNLOADS="`pwd`/${setup_download}"
    fi
else
    DOWNLOADS="$ROOTDIR/downloads"
fi
mkdir -p $DOWNLOADS
DOWNLOADS=`readlink -f "$DOWNLOADS"`

if [ -n "$setup_sstate" ]; then
    if echo $setup_sstate |grep -q ^/;then
        CACHES="${setup_sstate}"
    else
        CACHES="`pwd`/${setup_sstate}"
    fi
else
    is_not_ubuntu_machine
    if [ $? -eq 1 ]; then
        CACHES="$ROOTDIR/sstate-cache"
    else
        CACHES="$ROOTDIR/sstate-cache-ubuntu"
    fi
fi
mkdir -p $CACHES
CACHES=`readlink -f "$CACHES"`

# check if project folder was created before
if [ -e "$PROJECT_DIR/SOURCE_THIS" ]; then
    echo "$PROJECT_DIR was created before."
    . $PROJECT_DIR/SOURCE_THIS
    echo "Nothing is changed."
    clean_up && return
fi

# source oe-init-build-env to init build env
cd $OEROOTDIR
set -- $PROJECT_DIR
. ./oe-init-build-env > /dev/null

# if conf/local.conf not generated, no need to go further
if [ ! -e conf/local.conf ]; then
    echo "ERROR: the local.conf is not created, Exit ..."
    clean_up && cd $ROOTDIR && return
fi

# Remove comment lines and empty lines
sed -i -e '/^#.*/d' -e '/^$/d' conf/local.conf

# Change settings according to the environment
sed -e "s,MACHINE ??=.*,MACHINE ??= '$MACHINE',g" \
        -e "s,SDKMACHINE ??=.*,SDKMACHINE ??= '$SDKMACHINE',g" \
        -e "s,DISTRO ?=.*,DISTRO ?= '$DISTRO',g" \
        -i conf/local.conf

# Clean up PATH, because if it includes tokens to current directories somehow,
# wrong binaries can be used instead of the expected ones during task execution
export PATH="`echo $PATH | sed 's/\(:.\|:\)*:/:/g;s/^.\?://;s/:.\?$//'`"

# add layers
for layer in $(eval echo $LAYER_LIST); do
    append_layer=""
    if [ -e ${ROOTDIR}/${SOURCESDIR}/${layer} ]; then
        append_layer="${ROOTDIR}/${SOURCESDIR}/${layer}"
    fi
    if [ -n "${append_layer}" ]; then
        append_layer=`readlink -f $append_layer`
        awk '/  "/ && !x {print "'"  ${append_layer}"' \\"; x=1} 1' \
            conf/bblayers.conf > conf/bblayers.conf~
        mv conf/bblayers.conf~ conf/bblayers.conf

        # check if layer is compatible with supported yocto version.
        # if not, make it so.
        conffile_path="${append_layer}/conf/layer.conf"
        yocto_compatible=`grep "LAYERSERIES_COMPAT" "${conffile_path}" | grep "${YOCTOVERSION}"`
        if [ -z "${yocto_compatible}" ]; then
		    sed -E "/LAYERSERIES_COMPAT/s/(\".*)\"/\1 $YOCTOVERSION\"/g" -i "${conffile_path}"
		    echo Layer ${layer} updated for ${YOCTOVERSION}.
		fi
    fi
done

cat >> conf/local.conf <<-EOF

# Parallelism Options
BB_NUMBER_THREADS = "$THREADS"
PARALLEL_MAKE = "-j $JOBS"
DL_DIR = "$DOWNLOADS"
SSTATE_DIR = "$CACHES"
EOF

for s in $HOME/.oe $HOME/.yocto; do
    if [ -e $s/site.conf ]; then
        echo "Linking $s/site.conf to conf/site.conf"
        ln -s $s/site.conf conf
    fi
done

# option to enable lite mode for now
if test $setup_l; then
    echo "# delete sources after build" >> conf/local.conf
    echo "INHERIT += \"rm_work\"" >> conf/local.conf
    echo >> conf/local.conf
fi

if echo "$MACHINE" |egrep -q "^(b4|p5|t1|t2|t4)"; then
    # disable prelink (for multilib scenario) for now
    sed -i s/image-mklibs.image-prelink/image-mklibs/g conf/local.conf
fi

# Handle EULA setting
if [ -z "$EULA" ] && ! grep -q '^ACCEPT_FSL_EULA\s*=' conf/local.conf; then
    EULA='ask'
fi

if [ "$EULA" = "ask" ]; then
    cat <<EOF

Proprietary and third party software is subject to agreement and compliance
with, ${COMPANY}'s End User License Agreement. To have the right to use these
binaries in your images, you must read and accept the following terms.  If
there are conflicting terms embedded in the software, the terms embedded in
the Software will control.

In all cases,  open source software is licensed under the terms of the
applicable open source license(s), such as the BSD License, Apache License or
the GNU Lesser General Public License.  Your use of the open source software
is subject to the terms of each applicable license.  You must agree to the
terms of each applicable license, or you cannot use the open source software.

EOF

    sleep 5

    more -d $EULA_FILE
    echo
    while [ "$EULA" = "ask" ]; do
        echo -n "Do you accept the EULA you just read? (y/n) "
        read REPLY
        case "$REPLY" in
            y|Y)
            echo "EULA has been accepted."
            EULA="1"
            ;;
            n|N)
            echo "EULA has not been accepted."
            EULA="0"
            ;;
        esac
    done
fi

if grep -q '^ACCEPT_FSL_EULA\s*=' conf/local.conf; then
    sed -i "s/^#*ACCEPT_FSL_EULA\s*=.*/ACCEPT_FSL_EULA = \"$EULA\"/g" conf/local.conf
else
    echo "ACCEPT_FSL_EULA = \"$EULA\"" >> conf/local.conf
fi

# make a SOURCE_THIS file
if [ ! -e SOURCE_THIS ]; then
    echo "#!/bin/sh" >> SOURCE_THIS
    echo "cd $OEROOTDIR" >> SOURCE_THIS
    echo "set -- $PROJECT_DIR" >> SOURCE_THIS
    echo ". ./oe-init-build-env > /dev/null" >> SOURCE_THIS
    echo "echo \"Back to build project $PROJECT_DIR.\"" >> SOURCE_THIS
fi

prompt_message
cd $PROJECT_DIR
clean_up
