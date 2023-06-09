#! /usr/bin/env python3
# SPDX-License-Identifier: BSD-3-Clause
# Copyright 2023 NXP

"""
This module can be used to determine the path of a node based on compatible string
"""

import argparse
import pathlib
import fdt


def main():
    description = """Get the path of nodes that have a given "compatible" string."""
    parser = argparse.ArgumentParser(description=description, fromfile_prefix_chars='@')
    parser.add_argument('dtb_file', metavar='dtb_file', type=pathlib.Path,
                        nargs=1, help="Path to the dtb file to be processed")
    parser.add_argument('--compatible', type=str, help="The compatible string", required=True)
    args = parser.parse_args()

    dtb_file = args.dtb_file[0]
    with open(dtb_file, "rb") as dfile:
        dtb_data = dfile.read()

    linux_dt = fdt.parse_dtb(dtb_data)

    for path, _, props in linux_dt.walk():
        for prop in props:
            if prop.name == 'compatible':
                for compatible in prop.data:
                    if compatible == args.compatible:
                        print(path)


if __name__ == "__main__":
    main()

