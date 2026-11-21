#!/bin/sh
# rebuilds the code object; reference toolchain: clang and ld.lld 23.1.5 (Fedora 23.1.5-2.fc45)
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
