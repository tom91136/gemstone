cmake_minimum_required(VERSION 3.25)

set(artifacts target/conformance/artifacts)
set(output target/ci)

function(pack name)
  file(MAKE_DIRECTORY "${output}")
  execute_process(
    COMMAND tar -cf - ${ARGN}
    COMMAND zstd -T0 -q -12 --long=30 -f -o "${output}/${name}.tar.zst"
    COMMAND_ERROR_IS_FATAL ANY
  )
  file(SIZE "${output}/${name}.tar.zst" size)
  message(STATUS "${output}/${name}.tar.zst: ${size} bytes")
endfunction()

if(MODE STREQUAL "shard" AND DEFINED SHARD AND NOT SHARD STREQUAL "")
  separate_arguments(excluded UNIX_COMMAND "${EXCLUDED}")
  separate_arguments(owned UNIX_COMMAND "${OWNED}")
  list(TRANSFORM excluded PREPEND "--exclude=${artifacts}/")
  list(TRANSFORM owned PREPEND "${artifacts}/")
  pack("conformance-${SHARD}" ${excluded} "${artifacts}")
  if(owned)
    pack("conformance-${SHARD}-support" ${owned})
  endif()
elseif(MODE STREQUAL "full")
  file(REMOVE_RECURSE "${artifacts}")
  file(GLOB archives "${output}/conformance-*.tar.zst")
  if(NOT archives)
    message(FATAL_ERROR "No conformance-*.tar.zst in ${output}")
  endif()
  foreach(archive IN LISTS archives)
    execute_process(
      COMMAND zstd -dc --long=30 "${archive}"
      COMMAND tar -xf -
      COMMAND_ERROR_IS_FATAL ANY
    )
    file(REMOVE "${archive}")
  endforeach()
  file(GLOB shards LIST_DIRECTORIES TRUE "${artifacts}/sycl-shard-*")
  if(shards)
    execute_process(
      COMMAND "${CMAKE_COMMAND}" "-DARTIFACTS=${artifacts}" -P "${CMAKE_CURRENT_LIST_DIR}/Merge.cmake"
      RESULT_VARIABLE merged
    )
    if(NOT merged EQUAL 0)
      message(WARNING "The SYCL shards did not merge; the package leaves SYCL out")
      file(REMOVE_RECURSE "${artifacts}/sycl")
    endif()
    file(REMOVE_RECURSE ${shards})
  endif()
  pack(conformance "${artifacts}")
else()
  message(FATAL_ERROR
    "usage: cmake -DMODE=shard -DSHARD=<shard> [-DEXCLUDED=<paths>] [-DOWNED=<paths>] -P Package.cmake\n"
    "       cmake -DMODE=full -P Package.cmake")
endif()
