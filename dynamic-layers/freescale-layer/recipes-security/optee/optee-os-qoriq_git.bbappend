# We don't want to encode for a board, we want to encode for an SoC
# Unfortunately, the current sources don't permit that, but we can
# fix it up for the compatible machine at least.
OPTEEMACHINE_ls1043abluebox = "ls1043ardb"
OPTEEMACHINE_ls1046abluebox = "ls1046ardb"
COMPATIBLE_MACHINE = "(lx2160a.*)|(ls1043a|ls1046a|ls1012a)"
