# QEMU is only a Xen virtio/ioreq backend on target: headless, aarch64-only,
# and it never boots a guest through firmware.

# build dependency for --enable-xen
DEPENDS:append:class-target = " xen-tools"
# drop display/GPU/audio backends (libsdl2, Mesa, virgl, pulse, alsa)
PACKAGECONFIG:remove:class-target = "sdl virglrenderer epoxy alsa pulseaudio"
# build only the aarch64 emulator (meta 'qemu' pulls every built target)
QEMU_TARGETS:class-target = "aarch64"
# skip ~190 MB of firmware/ROM blobs in qemu-common
EXTRA_OECONF:append:class-target = " -Dinstall_blobs=false"
