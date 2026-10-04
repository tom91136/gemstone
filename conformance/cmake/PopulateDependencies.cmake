# Populate missing or changed pinned sources in the shared dependency cache.
# Run as: cmake -DCONFORMANCE_DEPENDENCY_CACHE=<dir> -P PopulateDependencies.cmake
cmake_minimum_required(VERSION 3.25)

include("${CMAKE_CURRENT_LIST_DIR}/DependencyCache.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/Dependencies.cmake")

conformance_dependency_cache_populate("${CONFORMANCE_DEPENDENCY_SOURCES}" "${CONFORMANCE_DEPENDENCY_CACHE}")
