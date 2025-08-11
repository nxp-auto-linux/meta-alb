python() {
    import re

    features = d.getVar("DISTRO_FEATURES").split()
    if "skip-uboot" not in features:
        return

    if "verifiedboot" in features or "secboot" in features or "xen" in features:
        raise SystemExit("DISTRO_FEATURES 'skip-uboot' and 'verifiedboot or secboot or xen' cannot be enabled at the same time.")

    machine = d.getVar('MACHINE') or ''

    # Extract SoC part from MACHINE
    soc_match = re.match(r'(s32g\d+)', machine)
    soc = soc_match.group(1) if soc_match else None

    # Only s32g399 and s32g274 SoCs are supported with this feature
    allowed_socs = ["s32g399", "s32g274"]
    if soc not in allowed_socs:
        raise SystemExit(f"Error: DISTRO_FEATURE 'skip-uboot' is only supported for SOCs {', '.join(allowed_socs)}. Current MACHINE='{machine}' implies unsupported SOC='{soc}'.")
}

