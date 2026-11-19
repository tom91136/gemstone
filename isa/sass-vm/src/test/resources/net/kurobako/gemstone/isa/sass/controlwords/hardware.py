#!/usr/bin/env python3
# usage: python3 -I hardware.py <regenerated dir> <output.tsv>
# runs every launch of hardware-spec.tsv on the card from the cubins beside this script and from the copies in
# <regenerated dir> (sbt "semanticsCheck/runMain net.kurobako.gemstone.isa.check.RegeneratedCubinRunner <dir> <cubins>"),
# requires the same bytes from both, and writes the hardware.tsv rows; needs clang++-19 and cuda.h ($CUDA_INCLUDE)
import os
import subprocess
import sys
import tempfile

HERE = os.path.dirname(os.path.abspath(__file__))
SETS, REPEATS, SEED = 4, 10, 103
CUDA_INCLUDE = os.environ.get("CUDA_INCLUDE", os.path.join(os.environ.get("CUDA_HOME", "/usr/local/cuda"), "include"))


def busy():
    apps = subprocess.run(["nvidia-smi", "--query-compute-apps=pid,name", "--format=csv,noheader"],
                          capture_output=True, text=True, check=True).stdout
    return [line for line in apps.splitlines() if line.strip() and "kwin_wayland" not in line]


def launch(binary, cubin, kernel, block, grid, arguments):
    command = [binary, cubin, kernel, str(block), str(grid), str(SETS), str(REPEATS), str(SEED), *arguments]
    sets = []
    for line in subprocess.run(command, capture_output=True, text=True, timeout=600, check=True).stdout.splitlines():
        parts = line.split()
        if parts[0] == "set":
            sets.append((parts[3].rstrip("|").split("|"), parts[5].rstrip("|").split("|"), parts[7]))
        else:
            raise SystemExit(f"{cubin} {kernel}: {line}")
    return sets


def main():
    regenerated, output = sys.argv[1], sys.argv[2]
    with tempfile.TemporaryDirectory() as work:
        binary = os.path.join(work, "launch")
        subprocess.run(["clang++-19", "-std=c++17", "-O1", f"-I{CUDA_INCLUDE}", os.path.join(HERE, "launch.cpp"), "-o", binary,
                        "-lcuda"], check=True)
        rows = []
        for line in open(os.path.join(HERE, "hardware-spec.tsv")):
            source, kernel, geometry, spec = line.rstrip("\n").split("\t")
            block, grid = map(int, geometry.split("x"))
            arguments = [a.replace(":T:", f":{block * grid}:") for a in spec.split()]
            while busy():
                print("waiting for", busy(), flush=True)
                subprocess.run(["sleep", "5"])
            cubin = f"{source}.sm_89.cubin"
            card = launch(binary, os.path.join(HERE, cubin), kernel, block, grid, arguments)
            ours = launch(binary, os.path.join(regenerated, cubin), kernel, block, grid, arguments)
            if card != ours or any(agree != f"{REPEATS}/{REPEATS}" for _, _, agree in card):
                raise SystemExit(f"{kernel}: regenerated cubin or repeats disagree with the original")
            for inputs, outputs, _ in card:
                values = [("b:" if a.startswith("buf:") else "s:") + hexed for a, hexed in zip(arguments, inputs)]
                rows.append("\t".join([cubin, kernel, str(block), str(grid), " ".join(values), "|".join(outputs)]))
            print(kernel, geometry, flush=True)
    with open(output, "w") as handle:
        handle.write("\n".join(rows) + "\n")


if __name__ == "__main__":
    main()
