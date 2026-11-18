#!/bin/sh
# rebuilds every object in manifest.tsv; reference compilers: clang and llc 23.1.5 (Fedora 23.1.5-2.fc45)
cd "$(dirname "$0")" || exit 1
tab=$(printf '\t')
while IFS=$tab read -r kernel target flags; do
  if [ -f "$kernel.c" ]; then
    clang -target amdgcn-amd-amdhsa -mcpu="$target" -O2 -nogpulib $flags -c -o "$kernel.$target.o" "$kernel.c" || exit 1
  else
    llc -mtriple=amdgcn-amd-amdhsa -mcpu="$target" -start-before=post-RA-hazard-rec -filetype=obj $flags -o "$kernel.$target.o" "$kernel.mir" || exit 1
  fi
done < manifest.tsv
