#!/bin/sh
# rebuilds every object in manifest.tsv; reference compiler: clang 23.1.5 (Fedora 23.1.5-2.fc45)
cd "$(dirname "$0")" || exit 1
tab=$(printf '\t')
while IFS=$tab read -r kernel target flags; do
  clang -target amdgcn-amd-amdhsa -mcpu="$target" -O2 -nogpulib $flags -c -o "$kernel.$target.o" "$kernel.c" || exit 1
done < manifest.tsv
