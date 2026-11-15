cmake_minimum_required(VERSION 3.25)
get_filename_component(conformance "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
execute_process(COMMAND mktemp -d /tmp/conformance-component-notices-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

function(fail text)
  file(REMOVE_RECURSE "${directory}")
  message(FATAL_ERROR "${text}")
endfunction()

function(make_artifact name)
  cmake_parse_arguments(PARSE_ARGV 1 artifact "" "" "LIBRARIES;MANIFEST;NOTICES")
  set(root "${directory}/${name}")
  file(MAKE_DIRECTORY "${root}/lib")
  foreach(library IN LISTS artifact_LIBRARIES)
    file(WRITE "${root}/lib/${library}" "")
  endforeach()
  foreach(entry IN LISTS artifact_MANIFEST)
    string(REPLACE "|" "\t" line "${entry}")
    string(APPEND manifest "${line}\n")
  endforeach()
  if(artifact_MANIFEST)
    file(WRITE "${root}/licences/runtime-libraries.tsv" "${manifest}")
  endif()
  foreach(entry IN LISTS artifact_NOTICES)
    string(REPLACE "|" "/" path "${entry}")
    get_filename_component(parent "${root}/licences/runtime/${path}" DIRECTORY)
    file(MAKE_DIRECTORY "${parent}")
    file(WRITE "${root}/licences/runtime/${path}" "notice\n")
  endforeach()
  set(${name} "${root}" PARENT_SCOPE)
endfunction()

file(WRITE "${directory}/probe.cmake"
  "include(\"${conformance}/cmake/ComponentNotices.cmake\")\n"
  "conformance_verify_component_notices(\"\${ROOT}\")\n"
  "message(STATUS \"notices verified\")\n")

function(probe root result_var output_var)
  execute_process(COMMAND "${CMAKE_COMMAND}" "-DROOT=${root}" -P "${directory}/probe.cmake"
    RESULT_VARIABLE result OUTPUT_VARIABLE output ERROR_VARIABLE error)
  set(${result_var} "${result}" PARENT_SCOPE)
  string(REGEX REPLACE "[ \t\r\n]+" " " output "${output}${error}")
  set(${output_var} "${output}" PARENT_SCOPE)
endfunction()

function(expect_pass name)
  probe("${${name}}" result output)
  if(NOT result EQUAL 0 OR NOT output MATCHES "notices verified")
    fail("${name} was rejected:\n${output}")
  endif()
endfunction()

function(expect_fail name pattern)
  probe("${${name}}" result output)
  if(result EQUAL 0)
    fail("${name} passed without its component notice")
  endif()
  if(NOT output MATCHES "${pattern}")
    fail("${name} failure does not name ${pattern}:\n${output}")
  endif()
endfunction()

make_artifact(missing_cuda
  LIBRARIES libcudart.so.13.3.29
  MANIFEST "cuda|/usr/local/cuda/targets/x86_64-linux/lib/libcudart.so.13.3.29|libcudart.so.13.3.29")
expect_fail(missing_cuda "cuda but installs no")

make_artifact(cuda
  LIBRARIES libcudart.so.13.3.29
  MANIFEST "cuda|/usr/local/cuda/targets/x86_64-linux/lib/libcudart.so.13.3.29|libcudart.so.13.3.29"
  NOTICES "cuda|NGC-DL-CONTAINER-LICENSE")
expect_pass(cuda)

make_artifact(missing_rocm
  LIBRARIES libamdhip64.so.7)
expect_fail(missing_rocm "rocm but installs no")

make_artifact(rocm
  LIBRARIES libamdhip64.so.7
  NOTICES "rocm|hip|LICENSE.md")
expect_pass(rocm)

make_artifact(missing_libgomp
  LIBRARIES libgomp.so.1.0.0)
expect_fail(missing_libgomp "libgomp but installs no")

make_artifact(chain
  LIBRARIES libcudart.so.13 libcudart.so.13.3.29
  MANIFEST "cuda|/usr/local/cuda/targets/x86_64-linux/lib/libcudart.so.13.3.29|libcudart.so.13.3.29"
  NOTICES "cuda|NGC-DL-CONTAINER-LICENSE")
expect_pass(chain)

make_artifact(symlink_target
  LIBRARIES libelf-0.190.so
  MANIFEST "system/libelf1t64|/lib/x86_64-linux-gnu/libelf.so.1|libelf.so.1"
  NOTICES "system|libelf1t64|copyright")
file(CREATE_LINK "libelf-0.190.so" "${symlink_target}/lib/libelf.so.1" SYMBOLIC)
expect_pass(symlink_target)

make_artifact(libgomp
  LIBRARIES libgomp.so.1.0.0
  NOTICES "libgomp|COPYING3")
expect_pass(libgomp)

# The component comes from the manifest, so an upstream the conformance tree
# has never seen needs its own notice rather than passing on a fixed list.
make_artifact(missing_unknown
  LIBRARIES libwibble.so.2
  MANIFEST "vendor/wibble|/opt/wibble/lib/libwibble.so.2|libwibble.so.2")
expect_fail(missing_unknown "vendor/wibble but installs no")

make_artifact(missing_system
  LIBRARIES libz.so.1
  MANIFEST "system/zlib1g|/usr/lib/x86_64-linux-gnu/libz.so.1|libz.so.1")
expect_fail(missing_system "system/zlib1g but installs no")

make_artifact(system
  LIBRARIES libz.so.1
  MANIFEST "system/zlib1g|/usr/lib/x86_64-linux-gnu/libz.so.1|libz.so.1"
  NOTICES "system|zlib1g|copyright")
expect_pass(system)

make_artifact(unknown
  LIBRARIES libwibble.so.2
  MANIFEST "vendor/wibble|/opt/wibble/lib/libwibble.so.2|libwibble.so.2"
  NOTICES "vendor|wibble|LICENSE")
expect_pass(unknown)

# A library added to an artifact without a manifest entry still has to be
# classified and covered.
make_artifact(unlisted
  LIBRARIES libcudart.so.13.3.29 libze_loader.so.1
  MANIFEST "cuda|/usr/local/cuda/targets/x86_64-linux/lib/libcudart.so.13.3.29|libcudart.so.13.3.29"
  NOTICES "cuda|NGC-DL-CONTAINER-LICENSE")
expect_fail(unlisted "levelzero but installs no")

make_artifact(unclassified
  LIBRARIES libwibble.so.2)
expect_fail(unclassified "no known notices")

file(WRITE "${directory}/install.cmake"
  "include(\"${conformance}/cmake/ComponentNotices.cmake\")\n"
  "conformance_install_component_notices(LIBRARIES \"\${LIBRARY}\"\n"
  "  DESTINATION \"\${ROOT}/licences/runtime\"\n"
  "  MANIFEST \"\${ROOT}/licences/runtime-libraries.tsv\")\n"
  "message(STATUS \"notices installed\")\n")

function(install_notices root library result_var output_var)
  execute_process(COMMAND "${CMAKE_COMMAND}" -E env ${ARGN}
    "${CMAKE_COMMAND}" "-DROOT=${root}" "-DLIBRARY=${library}" -P "${directory}/install.cmake"
    RESULT_VARIABLE result OUTPUT_VARIABLE output ERROR_VARIABLE error)
  set(${result_var} "${result}" PARENT_SCOPE)
  string(REGEX REPLACE "[ \t\r\n]+" " " output "${output}${error}")
  set(${output_var} "${output}" PARENT_SCOPE)
endfunction()

# NVHPC installs its notices under the compiler prefix, and carries libcudart from the CUDA tree it bundles.
set(nvhpc_image "${directory}/images/nvhpc/opt/nvidia/hpc_sdk/Linux_x86_64/26.5")
file(MAKE_DIRECTORY "${nvhpc_image}/compilers/lib" "${nvhpc_image}/compilers/license" "${nvhpc_image}/cuda/13.2")
file(WRITE "${nvhpc_image}/compilers/lib/libnvomp.so" "")
file(WRITE "${nvhpc_image}/compilers/license/LICENSE.txt" "NVIDIA HPC SDK\n")
file(WRITE "${nvhpc_image}/cuda/13.2/EULA.txt" "NVIDIA CUDA EULA\n")
set(nvhpc_artifact "${directory}/nvhpc")
install_notices("${nvhpc_artifact}" "${nvhpc_image}/compilers/lib/libnvomp.so" result output
  "NVHPC_ROOT=${nvhpc_image}")
if(NOT result EQUAL 0 OR NOT output MATCHES "notices installed")
  fail("the installed NVHPC tree went unnoticed:\n${output}")
endif()
file(GLOB_RECURSE nvhpc_notices LIST_DIRECTORIES FALSE "${nvhpc_artifact}/licences/runtime/nvhpc/*")
if(NOT (nvhpc_notices MATCHES "${nvhpc_image}/compilers/license/LICENSE.txt"
    AND nvhpc_notices MATCHES "${nvhpc_image}/cuda/13.2/EULA.txt"))
  fail("the NVHPC artifact installed other notices: ${nvhpc_notices}")
endif()
file(MAKE_DIRECTORY "${nvhpc_artifact}/lib")
file(WRITE "${nvhpc_artifact}/lib/libnvomp.so" "")
probe("${nvhpc_artifact}" result output)
if(NOT result EQUAL 0)
  fail("the installed NVHPC notices were rejected:\n${output}")
endif()

file(REMOVE_RECURSE "${directory}")
