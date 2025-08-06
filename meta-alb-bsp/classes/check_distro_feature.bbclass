python() {
    features = d.getVar("DISTRO_FEATURES").split()
    if ("skip-uboot" in features and "verifiedboot" in features) or \
       ("skip-uboot" in features and "secboot" in features) or \
       ("skip-uboot" in features and "xen" in features):
        raise SystemExit("DISTRO_FEATURES 'skip-uboot' and 'verifiedboot or secboot or xen' cannot be enabled at the same time.")
}

