# Generic helper for a persistent artifact build tree.
include_guard(GLOBAL)

# conformance_artifact_prepare_work(<directory> <signature> <out_directory>)
# Creates the directory and discards its build tree when the caller-provided
# configuration signature changes, so stale CMake cache entries cannot leak into
# a differently configured artifact.
function(conformance_artifact_prepare_work directory signature output)
  file(MAKE_DIRECTORY "${directory}")
  set(signature_file "${directory}/.conformance-signature")
  if(EXISTS "${signature_file}")
    file(READ "${signature_file}" previous)
    if(NOT previous STREQUAL signature)
      file(REMOVE_RECURSE "${directory}/build")
    endif()
  endif()
  file(WRITE "${signature_file}" "${signature}")
  set(${output} "${directory}" PARENT_SCOPE)
endfunction()
