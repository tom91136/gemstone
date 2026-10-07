#!/bin/sh
# Lifecycle handling only; CMake constructs commands and Make owns job tokens.
set -eu
script_directory=$(CDPATH='' cd -- "$(dirname -- "$0")" && pwd)

if [ "${1-}" = --stream ]; then
    progress=$2
    while IFS= read -r line || [ -n "$line" ]; do
        case "$line" in
            \[[0-9]*/*\]*)
                if [ -n "$progress" ]; then
                    printf '%s\n' "$line" > "$progress.new"
                    mv "$progress.new" "$progress"
                else
                    printf '%s\n' "$line"
                fi
                ;;
            *) printf '%s\n' "$line" ;;
        esac
    done
    exit 0
fi

if [ "${1-}" = --progress ]; then
    directory=$2
    sleeper=
    trap '[ -z "$sleeper" ] || kill "$sleeper" 2>/dev/null || :; exit 0' INT TERM
    while :; do
        for progress in "$directory"/*.progress; do
            [ -f "$progress" ] || continue
            if ! cmp -s "$progress" "$progress.seen"; then
                printf '[container] %s\n' "$(cat "$progress")"
                cp "$progress" "$progress.seen"
            fi
        done
        sleep 5 &
        sleeper=$!
        wait "$sleeper" || :
    done
fi

jobs=${CMAKE_BUILD_PARALLEL_LEVEL:-$(getconf _NPROCESSORS_ONLN)}
ninja=ninja
make='make'
cmake=${CMAKE:-cmake}
while [ "$#" -gt 0 ]; do
    case "$1" in
        --jobs) jobs=$2; shift 2 ;;
        --ninja) ninja=$2; shift 2 ;;
        --make) make=$2; shift 2 ;;
        --cmake) cmake=$2; shift 2 ;;
        --) shift; break ;;
        *) printf 'Unknown jobserver option: %s\n' "$1" >&2; exit 2 ;;
    esac
done
[ "$#" -gt 0 ] || { printf 'A build command is required after --\n' >&2; exit 2; }
command -v setsid >/dev/null
session=$(mktemp -d /tmp/jobserver-XXXXXX)
child=
monitor=
cleanup() {
    status=$?
    trap - 0
    trap '' INT TERM
    [ -z "$monitor" ] || kill "$monitor" 2>/dev/null || :
    [ -z "$child" ] || kill -TERM "-$child" 2>/dev/null || :
    cleanup_pids=
    for record in "$session"/*.runtime; do
        [ -f "$record" ] || continue
        "$cmake" -P "$script_directory/Jobserver.cmake" -- cleanup "${record%.runtime}" &
        cleanup_pids="$cleanup_pids $!"
    done
    for pid in $cleanup_pids; do wait "$pid" || :; done
    if [ -n "$child" ]; then
        remaining=50
        while kill -0 "$child" 2>/dev/null && [ "$remaining" -gt 0 ]; do
            sleep .1
            remaining=$((remaining - 1))
        done
        kill -KILL "-$child" 2>/dev/null || :
        wait "$child" 2>/dev/null || :
    fi
    [ -z "$monitor" ] || wait "$monitor" 2>/dev/null || :
    # A second pass covers a cidfile written during cancellation.
    for record in "$session"/*.runtime; do
        [ -f "$record" ] || continue
        "$cmake" -P "$script_directory/Jobserver.cmake" -- cleanup "${record%.runtime}"
    done
    rm -rf -- "$session"
    exit "$status"
}
trap cleanup 0
trap 'exit 130' INT TERM
"$cmake" -P "$script_directory/Jobserver.cmake" -- prepare "$session" "$jobs" "$ninja" "$make" "$@"
unset MAKEFLAGS MFLAGS MAKELEVEL CMAKE_BUILD_PARALLEL_LEVEL
export JOBSERVER_DIRECTORY="$session" TMPDIR="$session"
JOBSERVER_SERIAL=0
[ "$jobs" != 1 ] || JOBSERVER_SERIAL=1
export JOBSERVER_SERIAL
printf 'Shared build budget %s; GNU Make jobserver, Ninja clients\n' "$jobs"
sh "$0" --progress "$session" &
monitor=$!
setsid "$make" --no-print-directory --jobserver-style=fifo "-j$jobs" -f "$session/Makefile" &
child=$!
wait "$child"
