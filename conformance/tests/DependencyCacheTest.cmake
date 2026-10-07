cmake_minimum_required(VERSION 3.25)
include("${CMAKE_CURRENT_LIST_DIR}/../cmake/DependencyCache.cmake")

if(DEFINED MANIFEST)
  include("${MANIFEST}")
  return()
endif()

execute_process(COMMAND mktemp -d /tmp/dependency-cache-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)
set(cache "${directory}/cache")
set(manifest "${directory}/dependencies.cmake")
file(MAKE_DIRECTORY "${directory}/source/tree")
file(WRITE "${directory}/source/tree/value.txt" "unchanged archive\n")
execute_process(COMMAND "${CMAKE_COMMAND}" -E tar cf "${directory}/source.tar" tree
  WORKING_DIRECTORY "${directory}/source" COMMAND_ERROR_IS_FATAL ANY)
file(SHA256 "${directory}/source.tar" archive_hash)

function(write_manifest revision)
  file(WRITE "${directory}/source/header.hpp" "${revision}\n")
  file(SHA256 "${directory}/source/header.hpp" header_hash)
  file(WRITE "${manifest}"
    "pinned_dependency(header DOWNLOAD_NO_EXTRACT YES\n"
    "  URL file://${directory}/source/header.hpp URL_HASH SHA256=${header_hash})\n"
    "pinned_dependency(archive SOURCE_ONLY\n"
    "  URL file://${directory}/source.tar URL_HASH SHA256=${archive_hash})\n")
  set(header_hash "${header_hash}" PARENT_SCOPE)
endfunction()

function(run_manifest mode output)
  execute_process(COMMAND "${CMAKE_COMMAND}" "-DMANIFEST=${manifest}" "-DDEPENDENCY_CACHE=${cache}"
    "-DDEPENDENCY_CACHE_MODE=${mode}" ${ARGN} -P "${CMAKE_CURRENT_FUNCTION_LIST_FILE}"
    RESULT_VARIABLE status OUTPUT_VARIABLE out ERROR_VARIABLE error)
  set(${output} "${status}|${out}${error}" PARENT_SCOPE)
endfunction()

write_manifest("first revision")
run_manifest(verify result)
if(result MATCHES "^0\\|")
  message(FATAL_ERROR "Absent cache was accepted")
endif()
run_manifest(populate result)
run_manifest(verify result)
if(NOT result MATCHES "^0\\|")
  message(FATAL_ERROR "Freshly populated cache was rejected: ${result}")
endif()

# A populated archive must not be re-extracted when another pin changes.
file(WRITE "${cache}/archive/preserve.txt" "unchanged\n")
dependency_cache_apply("${cache}" header TRUE "${header_hash}")
if(NOT FETCHCONTENT_SOURCE_DIR_HEADER STREQUAL "${cache}/header")
  message(FATAL_ERROR "Matching cache was not used")
endif()

write_manifest("second revision")
run_manifest(verify result)
if(result MATCHES "^0\\|")
  message(FATAL_ERROR "Changed pin accepted stale source")
endif()
run_manifest(declare result -DDEPENDENCY_OFFLINE=ON)
if(result MATCHES "^0\\|" OR NOT result MATCHES "Missing or outdated cached dependency: header")
  message(FATAL_ERROR "Offline declaration did not reject the stale pin: ${result}")
endif()
dependency_cache_apply("${cache}" header FALSE "${header_hash}")
if(DEFINED FETCHCONTENT_SOURCE_DIR_HEADER OR FETCHCONTENT_FULLY_DISCONNECTED)
  message(FATAL_ERROR "Stale cache override or offline mode survived online invalidation")
endif()

run_manifest(populate result)
run_manifest(verify result)
file(READ "${cache}/header/header.hpp" content)
if(NOT result MATCHES "^0\\|" OR NOT content STREQUAL "second revision\n" OR NOT EXISTS "${cache}/archive/preserve.txt")
  message(FATAL_ERROR "Cache refresh changed the wrong entries or retained stale source: ${result}")
endif()
file(WRITE "${cache}/header/.pinned" "completed\n")
run_manifest(verify result)
if(result MATCHES "^0\\|")
  message(FATAL_ERROR "Unverified completion marker was accepted")
endif()
file(REMOVE_RECURSE "${directory}")
