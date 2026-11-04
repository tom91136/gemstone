include(FetchContent)
enable_language(HIP)
FetchContent_MakeAvailable(rocm_examples)

set(CMAKE_HIP_STANDARD 17)
set(GPU_RUNTIME HIP CACHE STRING "" FORCE)
set(ROCM_ROOT /opt/rocm CACHE PATH "" FORCE)
list(APPEND CMAKE_PREFIX_PATH /opt/rocm)

list(GET CMAKE_HIP_ARCHITECTURES 0 conformance_vendor_architecture)
string(REGEX REPLACE ":.*$" "" conformance_vendor_architecture "${conformance_vendor_architecture}")

include("${CMAKE_CURRENT_LIST_DIR}/hip/offload_targets.cmake")
foreach(library IN ITEMS rocblas rocfft rocrand rocsolver rocsparse)
  conformance_vendor_offload_targets(${library} conformance_vendor_${library}_targets)
endforeach()

set(conformance_vendor_example_directories
  HIP-Basic/bandwidth
  HIP-Basic/bit_extract
  HIP-Basic/cooperative_groups
  HIP-Basic/device_globals
  HIP-Basic/device_query
  HIP-Basic/dynamic_shared
  HIP-Basic/events
  HIP-Basic/gpu_arch
  HIP-Basic/hello_world
  HIP-Basic/inline_assembly
  HIP-Basic/matrix_multiplication
  HIP-Basic/module_api
  HIP-Basic/multi_gpu_data_transfer
  HIP-Basic/occupancy
  HIP-Basic/runtime_compilation
  HIP-Basic/saxpy
  HIP-Basic/shared_memory
  HIP-Basic/streams
  HIP-Basic/texture_management
  HIP-Basic/warp_shuffle
  Applications/bitonic_sort
  Applications/convolution
  Applications/floyd_warshall
  Applications/histogram
  Applications/monte_carlo_pi
  Applications/prefix_sum
  Libraries/rocPRIM
  Libraries/hipCUB
  Libraries/rocThrust
  Libraries/rocBLAS
  Libraries/rocRAND
  Libraries/rocFFT
  Libraries/rocSPARSE
  Libraries/rocSOLVER)

set(rocm_examples_staged "${CMAKE_CURRENT_BINARY_DIR}/rocm-examples-src")
stage_source("${rocm_examples_SOURCE_DIR}" "${rocm_examples_staged}.staging")
patch_exact("${rocm_examples_staged}.staging/HIP-Basic/saxpy/main.hip"
  [=[    HIP_CHECK(hipMemcpy(y.data(), d_y, size_bytes, hipMemcpyDeviceToHost));
]=]
  [=[    HIP_CHECK(hipMemcpy(y.data(), d_y, size_bytes, hipMemcpyDeviceToHost));
    for(unsigned int i = 0; i < size; i++)
    {
        if(y[i] != a * x[i] + 1.f)
        {
            return error_exit_code;
        }
    }
]=])
patch_exact("${rocm_examples_staged}.staging/HIP-Basic/streams/main.hip"
  [=[    deploy_multiple_stream<width, size>(h_in, h_transpose_matrix, num_streams);
]=]
  [=[    deploy_multiple_stream<width, size>(h_in, h_transpose_matrix, num_streams);
    for(unsigned int i = 0; i < num_streams; i++)
    {
        for(unsigned int r = 0; r < width; r++)
        {
            for(unsigned int c = 0; c < width; c++)
            {
                if(h_transpose_matrix[i][r * width + c] != h_in[c * width + r])
                {
                    return error_exit_code;
                }
            }
        }
    }
]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/rocPRIM/device_sum/main.hip"
  [=[    std::cout << "Reduced summation result: " << result << std::endl;
]=]
  [=[    if(result != 42)
    {
        return error_exit_code;
    }
    std::cout << "Reduced summation result: " << result << std::endl;
]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/hipCUB/device_sum/main.hip"
  [=[    std::cout << "Reduced summation result: " << result << std::endl;
]=]
  [=[    if(result != 42)
    {
        return error_exit_code;
    }
    std::cout << "Reduced summation result: " << result << std::endl;
]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/rocPRIM/block_sum/main.hip"
  [=[    HIP_CHECK(hipFree(d_in));
]=]
  [=[    if(h_out != expected_result)
    {
        return error_exit_code;
    }
    HIP_CHECK(hipFree(d_in));
]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/hipCUB/device_radix_sort/main.hip"
  [=[#include <cassert>
]=]
  [=[#include <algorithm>
#include <cassert>
]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/hipCUB/device_radix_sort/main.hip"
  [=[    HIP_CHECK(hipFree(d_keys.d_buffers[0]));
]=]
  [=[    std::vector<float> sorted_keys(h_keys);
    std::sort(sorted_keys.begin(), sorted_keys.end());
    if(result_keys != sorted_keys)
    {
        return error_exit_code;
    }
    for(int i = 0; i < num_elements; i++)
    {
        if(h_keys[result_values[i]] != result_keys[i])
        {
            return error_exit_code;
        }
    }
    HIP_CHECK(hipFree(d_keys.d_buffers[0]));
]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/rocSOLVER/getf2/main.cpp"
  [=[#include <cstdlib>
]=]
  [=[#include <cmath>
#include <cstdlib>
]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/rocSOLVER/getf2/main.cpp"
  [=[    HIP_CHECK(hipFree(d_A));
]=]
  [=[    std::vector<double> PA(A);
    for(int i = 0; i < std::min(m, n); i++)
    {
        for(int j = 0; j < n; j++)
        {
            std::swap(PA[i + j * lda], PA[(Ipiv[i] - 1) + j * lda]);
        }
    }
    for(int i = 0; i < m; i++)
    {
        for(int j = 0; j < n; j++)
        {
            double sum = 0;
            for(int k = 0; k <= std::min(i, j); k++)
            {
                sum += (k == i ? 1.0 : LU[i + k * lda]) * LU[k + j * lda];
            }
            if(std::fabs(sum - PA[i + j * lda]) > 1e-9)
            {
                return error_exit_code;
            }
        }
    }
    HIP_CHECK(hipFree(d_A));
]=])
foreach(example IN ITEMS rocFFT/complex_complex rocFFT/real_complex rocFFT/complex_real rocFFT/multi_gpu rocRAND/c_cpp_api/simple_distributions_cpp)
  patch_exact("${rocm_examples_staged}.staging/Libraries/${example}/main.cpp"
    [=[#include "example_utils.hpp"]=]
    [=[#include "example_utils.hpp"
#include "conformance_reference.hpp"]=])
endforeach()

patch_exact("${rocm_examples_staged}.staging/Libraries/rocFFT/complex_complex/main.cpp"
  [=[    print_nd_data(h_output, length, 1, 3, true);]=]
  [=[    const auto input = [&](const auto &index) { return h_input[conformance::reference::offset(index, stride)]; };
    if(!conformance::reference::matchesDft(length, length, stride, h_output.data(), inverse ? 1.0 : -1.0, input))
    {
        return error_exit_code;
    }
    print_nd_data(h_output, length, 1, 3, true);]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/rocFFT/callback/main.hip"
  [=[#include "example_utils.hpp"]=]
  [=[#include "example_utils.hpp"
#include "conformance_reference.hpp"]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/rocFFT/callback/main.hip"
  [=[    for(size_t i = 0; i < N; i++)
    {
        std::cout << "element " << i]=]
  [=[    const conformance::reference::Extent length{N};
    const conformance::reference::Extent stride{1};
    const auto                           filtered = [&](const auto& index)
    {
        const auto value = hipCmul(input[index[0]], callback_filter[index[0]]);
        return double2{value.x * scale_factor, value.y * scale_factor};
    };
    if(!conformance::reference::matchesDft(length, length, stride, output.data(), -1.0, filtered))
    {
        return error_exit_code;
    }

    for(size_t i = 0; i < N; i++)
    {
        std::cout << "element " << i]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/rocBLAS/level_2/her/main.cpp"
  [=[#include <complex>
]=]
  [=[#include <cmath>
#include <complex>
]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/rocBLAS/level_2/her/main.cpp"
  [=[            errors += diff.x > eps;
]=]
  [=[            errors += std::fabs(diff.x) > eps;
]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/rocBLAS/level_2/her/main.cpp"
  [=[            errors += diff.y > eps;
]=]
  [=[            errors += std::fabs(diff.y) > eps;
]=])
patch_exact("${rocm_examples_staged}.staging/Libraries/rocFFT/real_complex/main.cpp"
  [=[    print_nd_data(h_output, output_length, 1, 3, true);]=]
  [=[    const auto input = [&](const auto &index) { return h_input[conformance::reference::offset(index, input_stride)]; };
    if(!conformance::reference::matchesDft(length, output_length, output_stride, h_output.data(), -1.0, input))
    {
        return error_exit_code;
    }
    print_nd_data(h_output, output_length, 1, 3, true);]=])

patch_exact("${rocm_examples_staged}.staging/Libraries/rocFFT/complex_real/main.cpp"
  [=[    print_nd_data(h_output, length, 1, 3, true);]=]
  [=[    const auto input = [&](const auto &index) { return conformance::reference::hermitian(h_input.data(), length, input_stride, index); };
    if(!conformance::reference::matchesDft(length, length, output_stride, h_output.data(), 1.0, input))
    {
        return error_exit_code;
    }
    print_nd_data(h_output, length, 1, 3, true);]=])

patch_exact("${rocm_examples_staged}.staging/Libraries/rocFFT/multi_gpu/main.cpp"
  [=[designed to run on two devices with 3-D inputs!" << std::endl;
        return 0;]=]
  [=[designed to run on two devices with 3-D inputs!" << std::endl;
        return error_exit_code;]=])

patch_exact("${rocm_examples_staged}.staging/Libraries/rocFFT/multi_gpu/main.cpp"
  [=[    if(nDevices <= static_cast<int>(*std::max_element(devices.begin(), devices.end())))]=]
  [=[    if(nDevices < 2)
    {
        std::cout << "requires two devices" << std::endl;
        return 2;
    }
    if(nDevices <= static_cast<int>(*std::max_element(devices.begin(), devices.end())))]=])

patch_exact("${rocm_examples_staged}.staging/Libraries/rocFFT/multi_gpu/main.cpp"
  [=[    std::vector<size_t> brick_stride = {1, length[0] * length[1], length[0], idist};]=]
  [=[    std::vector<size_t> brick_stride = {1, length[0] / deviceCount, length[0] / deviceCount * length[1], idist};]=])

patch_exact("${rocm_examples_staged}.staging/Libraries/rocFFT/multi_gpu/main.cpp"
  [=[device ID greater than number of available devices" << std::endl;
        return 0;]=]
  [=[device ID greater than number of available devices" << std::endl;
        return error_exit_code;]=])

patch_exact("${rocm_examples_staged}.staging/Libraries/rocFFT/multi_gpu/main.cpp"
  [=[    // Destroy plan]=]
  [=[    std::vector<std::complex<double>> results(2 * bufferSize);
    HIP_CHECK(hipSetDevice(devices[0]));
    HIP_CHECK(hipMemcpy(results.data(), gpu_out[0], memSize, hipMemcpyDeviceToHost));
    HIP_CHECK(hipSetDevice(devices[1]));
    HIP_CHECK(hipMemcpy(results.data() + bufferSize, gpu_out[1], memSize, hipMemcpyDeviceToHost));
    const auto tolerance = 1e-9 * static_cast<double>(fftSize);
    const auto dc = std::count_if(results.begin(), results.end(), [&](const auto &value) { return std::abs(value - expected) < tolerance; });
    const auto zeros = std::count_if(results.begin(), results.end(), [&](const auto &value) { return std::abs(value) < tolerance; });
    if(dc != 1 || zeros != static_cast<std::ptrdiff_t>(results.size()) - 1)
    {
        return error_exit_code;
    }

    // Destroy plan]=])

patch_exact("${rocm_examples_staged}.staging/Libraries/rocRAND/c_cpp_api/simple_distributions_cpp/main.cpp"
  [=[            std::cout << std::endl;
        }
    };]=]
  [=[            std::cout << std::endl;
        }

        return result_vector;
    };]=])

patch_exact("${rocm_examples_staged}.staging/Libraries/rocRAND/c_cpp_api/simple_distributions_cpp/main.cpp"
  [=[    measure_time_and_print(
        [size]() { return generate_random_vector_on_device]=]
  [=[    const auto device_vector = measure_time_and_print(
        [size]() { return generate_random_vector_on_device]=])

patch_exact("${rocm_examples_staged}.staging/Libraries/rocRAND/c_cpp_api/simple_distributions_cpp/main.cpp"
  [=[    measure_time_and_print([size]()]=]
  [=[    const auto host_vector = measure_time_and_print([size]()]=])

patch_exact("${rocm_examples_staged}.staging/Libraries/rocRAND/c_cpp_api/simple_distributions_cpp/main.cpp"
  [=[                           "Random number generation on the host");]=]
  [=[                           "Random number generation on the host");
    if(!conformance::reference::sameMean(device_vector, host_vector))
    {
        exit(error_exit_code);
    }]=])

commit_staged_source("${rocm_examples_staged}.staging" "${rocm_examples_staged}")

foreach(directory IN LISTS conformance_vendor_example_directories)
  add_subdirectory("${rocm_examples_staged}/${directory}" "rocm-examples/${directory}")
endforeach()

include("${CMAKE_CURRENT_LIST_DIR}/hip/cases.cmake")

if(TARGET hip_module_api)
  install(FILES "$<TARGET_FILE_DIR:hip_module_api>/module.co" DESTINATION vendor-hip COMPONENT architecture)
endif()

file(GENERATE OUTPUT "${CMAKE_CURRENT_BINARY_DIR}/vendor_hip/cases.inc" CONTENT "${conformance_vendor_cases}")

add_executable(vendor_hip_examples hip/adapter.cpp)
conformance_project_target(vendor_hip_examples)
target_include_directories(vendor_hip_examples PRIVATE "${CMAKE_CURRENT_BINARY_DIR}/vendor_hip")
target_link_libraries(vendor_hip_examples PRIVATE Catch2::Catch2 conformance_device)
conformance_register_test_program(vendor_hip_examples ${CONFORMANCE_TIMEOUT_CTS})

foreach(library IN ITEMS rocfft rocsparse rocsolver rocrand)
  conformance_runtime_plugins("/opt/rocm/lib/lib${library}.so*")
endforeach()
conformance_runtime_plugins("/opt/rocm/lib/librocfft-device-*.so*")

include("${CMAKE_CURRENT_LIST_DIR}/hip/libraries.cmake")

list(APPEND conformance_vendor_targets vendor_hip_examples ${conformance_vendor_examples})
