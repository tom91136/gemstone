include("${CMAKE_CURRENT_LIST_DIR}/../cmake/Hecbench.cmake")

# CMake only exposes the CUDA compiler's feature table in the directory that enabled CUDA.
enable_language(CUDA)

FetchContent_MakeAvailable(hecbench)
set(source "${hecbench_SOURCE_DIR}")
set(staging "${CMAKE_CURRENT_BINARY_DIR}/source")
stage_source("${source}" "${staging}")

# Bounded workload: eight of the 1000 compress iterations (479.8 s on cuda-sass/sm_89 vs >600 s at 1000).
patch_exact("${staging}/src/bitpacking-cuda/kernels.cu"
  "for (int n = 0; n < 1000; n++)"
  "for (int n = 0; n < 8; n++)")

# Bounded workload: NUM 256 (3185 iterations, 299.9 s on cuda-sass/sm_89; default NUM 1024 needs 27250 iterations and its serial CPU reference exceeds 15 minutes).
patch_exact("${staging}/src/laplace-cuda/kernels.h"
  "#define NUM 1024"
  "#define NUM 256")

hecbench_dvc_entries("${source}" dvc_entries)
hecbench_licence_classification("${source}" licence_rows)
file(WRITE "${CMAKE_CURRENT_BINARY_DIR}/hecbench-licences.csv" "benchmark,licence,class,files\n")
file(WRITE "${CMAKE_CURRENT_BINARY_DIR}/hecbench-excluded.txt" "benchmark,licence,class\n")
set(candidates)
foreach(row IN LISTS licence_rows)
  string(REPLACE "|" ";" fields "${row}")
  list(GET fields 0 name)
  list(GET fields 1 licence)
  list(GET fields 2 class)
  list(GET fields 3 files)
  file(APPEND "${CMAKE_CURRENT_BINARY_DIR}/hecbench-licences.csv" "${name},${licence},${class},${files}\n")
  if(NOT (class STREQUAL "permissive" OR class STREQUAL "copyleft"))
    file(APPEND "${CMAKE_CURRENT_BINARY_DIR}/hecbench-excluded.txt" "${name},${licence},${class}\n")
    continue()
  endif()
  if(EXISTS "${staging}/src/${name}-cuda/CMakeLists.txt")
    list(APPEND candidates "${name}")
  endif()
endforeach()

find_program(CONFORMANCE_HECBENCH_FORTRAN NAMES gfortran flang nvfortran)
set(selected)
set(prerequisite_failures)
foreach(name IN LISTS candidates)
  file(READ "${staging}/src/${name}-cuda/CMakeLists.txt" project)
  hecbench_unsupported_cuda_features("${project}" unsupported_features)
  if(unsupported_features)
    list(JOIN unsupported_features ", " feature_list)
    list(APPEND prerequisite_failures
      "${name}|requires ${feature_list}, which CUDA ${CMAKE_CUDA_COMPILER_VERSION} does not provide")
    # HeCBench adds every *-cuda directory it finds, so hide it from the nested configure.
    file(REMOVE "${staging}/src/${name}-cuda/CMakeLists.txt")
  elseif(project MATCHES "enable_language\\(Fortran\\)" AND NOT CONFORMANCE_HECBENCH_FORTRAN)
    list(APPEND prerequisite_failures "${name}|no Fortran compiler in the CUDA 13.3 image")
  else()
    list(APPEND selected "${name}")
  endif()
endforeach()

set(referenced)
foreach(name IN LISTS selected)
  file(GLOB_RECURSE pointers "${source}/src/${name}-cuda/*.dvc")
  foreach(pointer IN LISTS pointers)
    string(REGEX REPLACE "\\.dvc$" "" archive "${pointer}")
    file(RELATIVE_PATH relative "${source}" "${archive}")
    list(APPEND referenced "${relative}")
  endforeach()
  file(READ "${source}/src/${name}-cuda/CMakeLists.txt" project)
  string(REGEX MATCHALL "UNZIPFILE\\([^)]*\\)" calls "${project}")
  foreach(call IN LISTS calls)
    string(REGEX REPLACE "^UNZIPFILE\\(\"?([^\")]*)\"?\\)$" "\\1" path "${call}")
    get_filename_component(filename "${path}" NAME)
    list(APPEND referenced "${filename}")
  endforeach()
endforeach()
foreach(entry IN LISTS dvc_entries)
  string(REPLACE "|" ";" fields "${entry}")
  list(GET fields 0 md5)
  list(GET fields 2 relative)
  get_filename_component(filename "${relative}" NAME)
  if(NOT (relative IN_LIST referenced) AND NOT (filename IN_LIST referenced))
    continue()
  endif()
  get_filename_component(parent "${relative}" DIRECTORY)
  file(MAKE_DIRECTORY "${staging}/${parent}")
  file(COPY_FILE "${DEPENDENCY_CACHE}/hecbench_dvc_${md5}/${filename}"
    "${staging}/${relative}" ONLY_IF_DIFFERENT)
endforeach()

set(target_lines)
foreach(name IN LISTS selected)
  string(REGEX REPLACE "[^A-Za-z0-9._-]" "_" program "${name}")
  list(APPEND target_lines "${name}|${name}-cuda|hecbench_${program}")
endforeach()
list(JOIN target_lines "\n" target_text)
file(WRITE "${CMAKE_CURRENT_BINARY_DIR}/targets.txt" "${target_text}\n")
list(JOIN prerequisite_failures "\n" prerequisite_text)
file(WRITE "${CMAKE_CURRENT_BINARY_DIR}/prerequisites.txt" "${prerequisite_text}\n")

set(CONFORMANCE_HECBENCH_JOBS 8 CACHE STRING "Concurrent HeCBench compiler jobs")
add_custom_target(conformance_hecbench_artifact
  COMMAND "${CMAKE_COMMAND}"
    "-DSOURCE=${staging}"
    "-DBUILD=${CMAKE_CURRENT_BINARY_DIR}/build"
    "-DDATA=${CMAKE_CURRENT_BINARY_DIR}/hecbench-data"
    "-DTARGETS=${CMAKE_CURRENT_BINARY_DIR}/targets.txt"
    "-DPREREQUISITES=${CMAKE_CURRENT_BINARY_DIR}/prerequisites.txt"
    "-DPROGRAMS=${CMAKE_CURRENT_BINARY_DIR}/hecbench-programs.txt"
    "-DBUILT=${CMAKE_CURRENT_BINARY_DIR}/built.txt"
    "-DFAILURES=${CMAKE_CURRENT_BINARY_DIR}/hecbench-build-failures.txt"
    "-DCXXOPTS=${DEPENDENCY_CACHE}/cxxopts"
    "-DFMT=${DEPENDENCY_CACHE}/hecbench_fmt"
    "-DCUDA_ARCH=${conformance_cuda_architecture}"
    "-DJOBS=${CONFORMANCE_HECBENCH_JOBS}"
    "-DTIMEOUT=${CONFORMANCE_TIMEOUT_HECBENCH}"
    -P "${CMAKE_CURRENT_LIST_DIR}/Build.cmake"
  COMMENT "Building HeCBench CUDA benchmarks"
  VERBATIM)

file(MAKE_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/hecbench-data/tree")
install(DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/hecbench-data/tree/"
  DESTINATION data/hecbench/tree COMPONENT support)
install(FILES "${CMAKE_CURRENT_BINARY_DIR}/hecbench-data/hecbench-arguments.json"
  "${CMAKE_CURRENT_BINARY_DIR}/hecbench-data/hecbench-directories.json"
  "${CMAKE_CURRENT_BINARY_DIR}/hecbench-data/hecbench-missing-data.txt"
  "${CMAKE_CURRENT_BINARY_DIR}/hecbench-data/hecbench-unparsed-runs.txt"
  DESTINATION data/hecbench COMPONENT support)
install(FILES "${CMAKE_CURRENT_BINARY_DIR}/hecbench-licences.csv"
  "${CMAKE_CURRENT_BINARY_DIR}/hecbench-excluded.txt"
  DESTINATION data/hecbench COMPONENT support)
install(FILES "${CMAKE_CURRENT_BINARY_DIR}/hecbench-build-failures.txt"
  DESTINATION data/hecbench COMPONENT architecture OPTIONAL)
install(CODE "
  set(prefix \"\${CMAKE_INSTALL_PREFIX}\")
  if(EXISTS \"${CMAKE_CURRENT_BINARY_DIR}/built.txt\")
    file(STRINGS \"${CMAKE_CURRENT_BINARY_DIR}/built.txt\" entries)
    foreach(entry IN LISTS entries)
      string(REPLACE \"|\" \";\" fields \"\${entry}\")
      list(GET fields 0 program)
      list(GET fields 1 binary)
      if(EXISTS \"\${binary}\")
        file(INSTALL FILES \"\${binary}\" DESTINATION \"\${prefix}\" RENAME \"\${program}\")
        file(CHMOD \"\${prefix}/\${program}\" PERMISSIONS
          OWNER_READ OWNER_WRITE OWNER_EXECUTE GROUP_READ GROUP_EXECUTE WORLD_READ WORLD_EXECUTE)
        file(APPEND \"\${prefix}/hecbench-test-programs.txt\" \"\${program}\t${CONFORMANCE_TIMEOUT_HECBENCH}\thecbench\n\")
      endif()
    endforeach()
  endif()
" COMPONENT architecture)
