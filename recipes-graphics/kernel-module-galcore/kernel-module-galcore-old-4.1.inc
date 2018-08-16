
# For kernel 4.1, we use an older revision
SRCREV = "27d109df067486855b6b58b8da9adff2200a003d"

# Update part version, this revision is for galcore 6.2.2
PV = "6.2.2"

SRC_URI += "\
    file://0001-Wno-misleading-indentation.patch \
"
