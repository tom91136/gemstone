#!/bin/sh
# rebuilds the code objects; reference toolchain: clang and ld.lld 23.1.5 (Fedora 23.1.5-2.fc45), ptxas 13.3.73
cd "$(dirname "$0")" || exit 1
clang -target amdgcn-amd-amdhsa -mcpu=gfx1036 -O2 -nogpulib -c -o fixtures.gfx1036.o fixtures.c || exit 1
ld.lld -shared -o fixtures.gfx1036.hsaco fixtures.gfx1036.o || exit 1
rm fixtures.gfx1036.o
clang -x cl -cl-std=CL2.0 -target amdgcn-amd-amdhsa -mcpu=gfx1036 -O2 -nogpulib -c -o hidden.gfx1036.o hidden.cl || exit 1
ld.lld -shared -o hidden.gfx1036.hsaco hidden.gfx1036.o || exit 1
rm hidden.gfx1036.o
hipcc --offload-arch=gfx1036 --offload-device-only -O3 -c -o flux.gfx1036.bundle "$(git rev-parse --show-toplevel)"/tools/benchmarks/fixtures/cloverleaf-flux.cu || exit 1
clang-offload-bundler --unbundle --type=o --input=flux.gfx1036.bundle --targets=hipv4-amdgcn-amd-amdhsa--gfx1036 --output=flux.gfx1036.hsaco || exit 1
rm flux.gfx1036.bundle
clang -target nvptx64-nvidia-cuda -march=sm_89 -Xclang -target-feature -Xclang +ptx78 -O2 -S -o typed.sm_89.ptx typed.c || exit 1
ptxas -arch=sm_89 -o typed.sm_89.cubin typed.sm_89.ptx || exit 1
rm typed.sm_89.ptx
