set(vendor_cusparse_samples
  axpby dense2sparse_csr gather rot scatter sddmm_csr sparse2dense_csr spgemm spmm_csr spmv_coo
  spsv_csr spvv)
set(vendor_cusparse_release_samples bicgstab cg coosort)
set(vendor_cusparse_shrunk_samples bicgstab cg)
if(NOT CUDAToolkit_VERSION VERSION_LESS 12)
  list(APPEND vendor_cusparse_samples spmv_csr)
endif()

# The samples are single-file programs whose only external symbol is main, so one binary hosts all of them.
set(vendor_cusparse_cases)
foreach(sample IN LISTS vendor_cusparse_samples vendor_cusparse_release_samples)
  file(GLOB source "${vendor_library_root}/cuSPARSE/${sample}/*_example.c*")
  list(LENGTH source source_count)
  if(NOT source_count EQUAL 1)
    message(FATAL_ERROR "Expected one cuSPARSE example in ${sample}")
  endif()
  set(tags)
  if(sample IN_LIST vendor_cusparse_release_samples)
    set(tags "[release]")
  endif()
  if(sample IN_LIST vendor_cusparse_shrunk_samples)
    get_filename_component(name "${source}" NAME)
    set(staged "${vendor_programs_directory}/cusparse_${sample}")
    stage_source("${vendor_library_root}/cuSPARSE/${sample}" "${staged}.staging")
    patch_exact("${staged}.staging/${name}" "int grid = 700;" "int grid = 64;")
    commit_staged_source("${staged}.staging" "${staged}")
    set(source "${staged}/${name}")
  endif()
  list(APPEND vendor_cusparse_cases "${sample}|vendor/cusparse/${sample}|${source}||${tags}")
endforeach()
conformance_vendor_cuda_program(vendor_cuda_cusparse
  TAG "[vendor][cusparse]"
  CASES ${vendor_cusparse_cases}
  LIBRARIES CUDA::cusparse CUDA::cublas CUDA::nvrtc $<$<TARGET_EXISTS:CUDA::nvJitLink>:CUDA::nvJitLink>)
