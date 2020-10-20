# We don't want to encode for a board, we want to encode for an SoC
# Unfortunately, the current sources don't permit that, but we can
# fix it up for the compatible machine at least.
OPTEEMACHINE_ls1043abluebox = "ls1043ardb"
OPTEEMACHINE_ls1046abluebox = "ls1046ardb"
OPTEEMACHINE_ls1012abluebox = "ls1012ardb"
COMPATIBLE_MACHINE_append = "|(ls1043a|ls1046a|ls1012a)"
