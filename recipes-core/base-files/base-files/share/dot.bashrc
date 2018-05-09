# ~/.bashrc: executed by bash(1) for non-login shells.

export PS1='\h:\w\$ '
umask 022

# Tools like Midnight Commander want to see this variable
if [ -e /bin/bash ]; then
     export SHELL=/bin/bash
fi

# User specific aliases and functions

bb() {
        cd $BUILDDIR
        bitbake $@
        cd - >/dev/null
}

bbl() {
        cd $BUILDDIR
        bitbake-layers $@
        cd - >/dev/null
}


# You may uncomment the following lines if you want `ls' to be colorized:
export LS_OPTIONS='--color=auto'
eval `dircolors`
alias ls='ls $LS_OPTIONS'
alias ll='ls $LS_OPTIONS -l'
# alias l='ls $LS_OPTIONS -lA'
#
# Some more alias to avoid making mistakes:
# alias rm='rm -i'
# alias cp='cp -i'
# alias mv='mv -i'
