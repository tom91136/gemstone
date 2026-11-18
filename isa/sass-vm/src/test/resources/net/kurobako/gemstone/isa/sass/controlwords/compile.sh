#!/bin/sh
# rebuilds every cubin in manifest.tsv; reference compiler: nvcc and ptxas 13.3.73 (cuda_13.3.r13.3/compiler.38244171_0)
cd "$(dirname "$0")" || exit 1
tab=$(printf '\t')
while IFS=$tab read -r source target; do
  nvcc -ccbin /usr/bin/g++-11 -cubin -arch="$target" -o "$source.$target.cubin" "$source.cu" || exit 1
done < manifest.tsv
