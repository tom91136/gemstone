include_guard(GLOBAL)

# the component follows where the library came from, not which libraries were expected
function(conformance_runtime_component path output)
  get_filename_component(name "${path}" NAME)
  if(path MATCHES "^/usr/local/cuda[^/]*/")
    set(component cuda)
  elseif(path MATCHES "/nvidia/hpc_sdk/")
    set(component nvhpc)
  elseif(path MATCHES "^/opt/intel/oneapi/")
    set(component oneapi)
  elseif(path MATCHES "^/opt/rocm/")
    set(component rocm)
  elseif(path MATCHES "^/usr/lib/llvm-|^/usr/local/lib/llvm|^/opt/llvm/")
    set(component llvm)
  else()
    conformance_library_component("${name}" component)
  endif()
  if(NOT component AND IS_ABSOLUTE "${path}" AND EXISTS "${path}")
    get_filename_component(real "${path}" REALPATH)
    execute_process(COMMAND dpkg-query -S "${real}"
      OUTPUT_VARIABLE ownership ERROR_QUIET RESULT_VARIABLE status)
    if(status EQUAL 0 AND ownership MATCHES "^([^,:]+)")
      set(component "system/${CMAKE_MATCH_1}")
    endif()
  endif()
  set(${output} "${component}" PARENT_SCOPE)
endfunction()

function(conformance_library_component name output)
  if(name MATCHES "^libgomp")
    set(component libgomp)
  elseif(name MATCHES "^lib(cudart|cublas(Lt)?|cufftw?|curand|cusparse|cusolver(Mg)?|nvJitLink|nvrtc|nvToolsExt)")
    set(component cuda)
  elseif(name MATCHES "^lib(acc|accg|acch|accm|cudanvhpc|nvc|nvcpud|nvf|nvhpc|nvomp|pg)")
    set(component nvhpc)
  elseif(name MATCHES "^lib(sycl|ur_|intelocl|mkl|tbb|umf|iomp|imf|irc|svml|ifcore)")
    set(component oneapi)
  elseif(name MATCHES "^lib(amdhip64|hsa-runtime64|hsa-amd-aqlprofile|omptarget|roc|hip|amd_comgr|hsakmt)")
    set(component rocm)
  elseif(name MATCHES "^libvulkan")
    set(component vulkan)
  elseif(name MATCHES "^libOpenCL")
    set(component opencl)
  elseif(name MATCHES "^libze_loader")
    set(component levelzero)
  elseif(name MATCHES "^lib(omp|LLVMOffload)")
    set(component llvm)
  else()
    set(component "")
  endif()
  set(${output} "${component}" PARENT_SCOPE)
endfunction()

# matches are copied verbatim from files the toolchain image already carries
function(conformance_component_notice_files component output)
  set(files)
  if(component STREQUAL "cuda")
    file(GLOB files LIST_DIRECTORIES false
      "/usr/local/share/licenses/cuda/*"
      "/usr/local/cuda*/EULA.txt"
      "/usr/local/cuda*/LICENSE*")
  elseif(component STREQUAL "nvhpc")
    file(GLOB files LIST_DIRECTORIES false
      "$ENV{NVHPC_ROOT}/*licen*"
      "$ENV{NVHPC_ROOT}/*EULA*"
      "$ENV{NVHPC_ROOT}/compilers/*licen*"
      "$ENV{NVHPC_ROOT}/compilers/*EULA*"
      "$ENV{NVHPC_ROOT}/compilers/*/licen*"
      "$ENV{NVHPC_ROOT}/compilers/*/EULA*"
      "/opt/nvidia/hpc_sdk/*licen*"
      "/opt/nvidia/hpc_sdk/*EULA*"
      "/opt/nvidia/hpc_sdk/Linux_x86_64/*/*licen*"
      "/opt/nvidia/hpc_sdk/Linux_x86_64/*/*EULA*"
      "/opt/nvidia/hpc_sdk/Linux_x86_64/*/compilers/*licen*"
      "/opt/nvidia/hpc_sdk/Linux_x86_64/*/compilers/*EULA*")
  elseif(component STREQUAL "oneapi")
    file(GLOB files LIST_DIRECTORIES false
      "/opt/intel/oneapi/licensing/*/licensing/*/*"
      "/opt/intel/oneapi/*/*/share/doc/*/LICENSE*"
      "/opt/intel/oneapi/*/*/share/doc/*/licensing/*")
  elseif(component STREQUAL "rocm")
    file(GLOB files LIST_DIRECTORIES false
      "/opt/rocm/share/doc/*/LICENSE*"
      "/opt/rocm/share/doc/*/License*"
      "/opt/rocm/share/doc/*/NOTICES*"
      "/opt/rocm/share/doc/*/copyright"
      "/opt/rocm/LICENSE*")
  elseif(component STREQUAL "libgomp")
    file(GLOB files LIST_DIRECTORIES false
      "/usr/local/share/licenses/libgomp/*"
      "/usr/share/doc/libgomp*/copyright"
      "/usr/share/doc/gcc-*-base/copyright")
  elseif(component STREQUAL "vulkan")
    file(GLOB files LIST_DIRECTORIES false
      "/usr/share/doc/libvulkan*/copyright"
      "/usr/share/doc/mesa-vulkan-drivers/copyright")
  elseif(component STREQUAL "opencl")
    file(GLOB files LIST_DIRECTORIES false
      "/usr/share/doc/ocl-icd-libopencl1/copyright"
      "/usr/share/doc/opencl-icd*/copyright")
  elseif(component STREQUAL "levelzero")
    file(GLOB files LIST_DIRECTORIES false
      "/usr/share/doc/libze1/copyright"
      "/usr/share/doc/libze-dev/copyright"
      "/usr/share/doc/level-zero-loader/copyright")
  elseif(component STREQUAL "llvm")
    file(GLOB files LIST_DIRECTORIES false
      "/usr/share/doc/llvm-*/copyright"
      "/usr/lib/llvm-*/LICENSE.TXT"
      "/opt/llvm/LICENSE.TXT")
  elseif(component MATCHES "^system/(.+)$")
    file(GLOB files "/usr/share/doc/${CMAKE_MATCH_1}/copyright")
  endif()
  list(FILTER files EXCLUDE REGEX "/$")
  set(${output} ${files} PARENT_SCOPE)
endfunction()

function(conformance_install_component_notices)
  cmake_parse_arguments(PARSE_ARGV 0 notice "" "DESTINATION;MANIFEST" "LIBRARIES")
  if(NOT notice_DESTINATION OR NOT notice_MANIFEST)
    message(FATAL_ERROR "conformance_install_component_notices requires DESTINATION and MANIFEST")
  endif()
  set(libraries ${notice_LIBRARIES})
  list(REMOVE_DUPLICATES libraries)
  set(lines)
  set(components)
  foreach(library IN LISTS libraries)
    conformance_runtime_component("${library}" component)
    if(NOT component)
      message(FATAL_ERROR "Cannot identify the component that supplies ${library}")
    endif()
    get_filename_component(filename "${library}" NAME)
    list(APPEND lines "${component}\t${library}\t${filename}")
    list(APPEND components "${component}")
  endforeach()
  list(REMOVE_DUPLICATES components)
  foreach(component IN LISTS components)
    conformance_component_notice_files("${component}" files)
    if(NOT files)
      message(FATAL_ERROR "No licence files are installed in the image for ${component}")
    endif()
    foreach(file IN LISTS files)
      file(RELATIVE_PATH relative "/" "${file}")
      set(destination "${notice_DESTINATION}/${component}/${relative}")
      get_filename_component(parent "${destination}" DIRECTORY)
      file(MAKE_DIRECTORY "${parent}")
      file(COPY_FILE "${file}" "${destination}" ONLY_IF_DIFFERENT)
    endforeach()
  endforeach()
  list(SORT lines)
  get_filename_component(parent "${notice_MANIFEST}" DIRECTORY)
  file(MAKE_DIRECTORY "${parent}")
  list(JOIN lines "\n" content)
  file(WRITE "${notice_MANIFEST}" "${content}\n")
endfunction()

function(conformance_soname filename output)
  string(REGEX REPLACE "^(.*\\.so)(\\..*)?$" "\\1" soname "${filename}")
  set(${output} "${soname}" PARENT_SCOPE)
endfunction()

function(conformance_verify_component_notices root)
  if(NOT IS_DIRECTORY "${root}")
    message(FATAL_ERROR "Artifact root does not exist: ${root}")
  endif()
  set(manifest "${root}/licences/runtime-libraries.tsv")
  if(EXISTS "${manifest}")
    file(STRINGS "${manifest}" entries)
    foreach(entry IN LISTS entries)
      string(REPLACE "\t" ";" fields "${entry}")
      list(LENGTH fields count)
      if(NOT count EQUAL 3)
        message(FATAL_ERROR "Malformed runtime library manifest entry in ${manifest}: ${entry}")
      endif()
      list(GET fields 0 component)
      list(GET fields 2 filename)
      set("recorded_${filename}" "${component}")
      conformance_soname("${filename}" soname)
      if(NOT DEFINED "recorded_soname_${soname}")
        set("recorded_soname_${soname}" "${component}")
      endif()
    endforeach()
  endif()

  file(GLOB libraries LIST_DIRECTORIES FALSE "${root}/lib/*.so*")
  foreach(library IN LISTS libraries)
    get_filename_component(filename "${library}" NAME)
    conformance_soname("${filename}" soname)
    if(DEFINED "recorded_${filename}")
      set(component "${recorded_${filename}}")
    elseif(DEFINED "recorded_soname_${soname}")
      set(component "${recorded_soname_${soname}}")
    else()
      conformance_library_component("${filename}" component)
    endif()
    if(NOT component)
      get_filename_component(real "${library}" REALPATH)
      foreach(candidate IN LISTS libraries)
        if(IS_SYMLINK "${candidate}")
          get_filename_component(candidate_real "${candidate}" REALPATH)
          if(candidate_real STREQUAL real)
            get_filename_component(candidate_name "${candidate}" NAME)
            if(DEFINED "recorded_${candidate_name}")
              set(component "${recorded_${candidate_name}}")
              break()
            endif()
          endif()
        endif()
      endforeach()
    endif()
    if(NOT component)
      message(FATAL_ERROR "Artifact ${root} bundles ${filename}, whose component has no known notices")
    endif()
    file(GLOB_RECURSE notices LIST_DIRECTORIES FALSE
      "${root}/licences/runtime/${component}/*")
    if(NOT notices)
      message(FATAL_ERROR
        "Artifact ${root} bundles ${filename} from ${component} but installs no ${component} notices")
    endif()
  endforeach()
endfunction()
