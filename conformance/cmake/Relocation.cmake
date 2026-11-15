include_guard(GLOBAL)

# Paths of the machine that produced an artifact rather than the artifact itself. A copied tree must not carry them,
# so the relocation check rejects them and generated text rewrites them.
function(conformance_relocation_roots output)
  set(${output}
    "/artifact/" "/build/" "/source/" "/work/" "/opt/rocm/" "/opt/intel/" "/usr/local/cuda/"
    PARENT_SCOPE)
endfunction()

function(conformance_relocatable_text text output)
  conformance_relocation_roots(roots)
  foreach(root IN LISTS roots)
    string(REGEX REPLACE "^.*/([^/]+)/$" "\\1" name "${root}")
    string(REPLACE "${root}" "/<${name}>/" text "${text}")
  endforeach()
  set(${output} "${text}" PARENT_SCOPE)
endfunction()
