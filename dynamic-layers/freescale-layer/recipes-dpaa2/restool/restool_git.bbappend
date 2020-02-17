# Original recipe unfortunately encodes for boards, not for SoCs.
# restool is board independent but SoC dependent.
COMPATIBLE_MACHINE += "|(ls2080a|ls2088a|ls1043a|ls1046a|ls1088a)"
COMPATIBLE_MACHINE_fsl-lsch3_append = "|(ls2080a|ls2088a)"