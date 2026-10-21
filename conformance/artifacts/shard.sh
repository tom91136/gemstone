#!/bin/sh
# Prints the route selectors of one shard of a target set. The architectures of the given backends are cut, in route
# order, into equal consecutive slices, so every route lands in exactly one shard, a selector keeps the compilers of its
# architecture together, and neighbouring architectures of a family share a shard.
#   sh shard.sh <target-set> <index> <count> <backend>...
set -eu
script_directory=$(CDPATH='' cd -- "$(dirname -- "$0")" && pwd)
target_set=$1
index=$2
count=$3
shift 3

routes=$(cmake "-DCONFORMANCE_TARGET_SET=$target_set" -P "$script_directory/Targets.cmake")
printf '%s\n' "$routes" | sed -E 's#^cuda-(ptx|sass)/#cuda/#' | cut -d/ -f1,2 |
    awk -v shard="$index" -v count="$count" -v backends="$*" '
        BEGIN { n = split(backends, names, " "); for (i = 1; i <= n; i++) wanted[names[i]] = 1 }
        {
            split($0, field, "/")
            if (!(field[1] in wanted) || $0 in seen) next
            seen[$0] = 1
            if (!(field[2] in unit)) unit[field[2]] = units++
            selector[++selectors] = $0
            architecture[selectors] = field[2]
        }
        END {
            for (i = 1; i <= selectors; i++)
                if (int(unit[architecture[i]] * count / units) == shard) print selector[i]
        }'
