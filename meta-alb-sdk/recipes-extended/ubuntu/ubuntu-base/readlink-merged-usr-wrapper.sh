#!/bin/sh
# readlink wrapper invoked via PSEUDO_CHROOT_XTRANSLATION during noble
# rootfs construction.
#
# Why this exists:
#   Noble's systemd preinst aborts if
#       readlink -f "$DPKG_ROOT/bin" != "$DPKG_ROOT/usr/bin"
#   Pseudo's chroot-aware readlink returns host paths instead of the
#   expected chroot-relative path, breaking the equality check even
#   though the rootfs IS merged-/usr.
#
#   Note the string comparison is LITERAL: if DPKG_ROOT="/" then the
#   input is "//bin" and the expected output is "//usr/bin" (preserve
#   the prefix exactly, including any extra slashes).
#
# Strategy:
#   If the args look like `-f <prefix>/bin` (or sbin/lib), echo
#   `<prefix>/usr/bin` (etc.), preserving the prefix exactly.
#   For other invocations, delegate to host coreutils `realpath`
#   (different basename so it won't re-trigger XTRANSLATION recursion).

if [ "$1" = "-f" ] && [ -n "$2" ]; then
    case "$2" in
        */bin)  echo "${2%/bin}/usr/bin";   exit 0 ;;
        */sbin) echo "${2%/sbin}/usr/sbin"; exit 0 ;;
        */lib)  echo "${2%/lib}/usr/lib";   exit 0 ;;
    esac
fi

# Fallback for non-merged-/usr cases: use host realpath (basename
# `realpath` does not match XTRANSLATION pattern `readlink`, so no
# infinite recursion).
args=""
for a in "$@"; do
    case "$a" in
        -f|-e|-m) ;;                       # ignore readlink-only flags
        *) args="$args $a" ;;
    esac
done
# shellcheck disable=SC2086
exec /usr/bin/realpath $args
