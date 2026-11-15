cmake_minimum_required(VERSION 3.25)
include("${CMAKE_CURRENT_LIST_DIR}/../cmake/Hecbench.cmake")

execute_process(COMMAND mktemp -d /tmp/hecbench-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

function(write path content)
  get_filename_component(parent "${path}" DIRECTORY)
  file(MAKE_DIRECTORY "${parent}")
  file(WRITE "${path}" "${content}")
endfunction()

write("${directory}/src/CMakeLists.txt" [=[
set(HECBENCH_POC_BENCHMARKS
    alpha
    beta
    gamma
    delta
    epsilon
    #commented
    zeta
    eta
    theta
)
]=])
write("${directory}/src/alpha-cuda/CMakeLists.txt" "")
write("${directory}/src/alpha-cuda/LICENSE" "MIT License\n\nPermission is hereby granted, free of charge, to any person obtaining a copy.\n")
write("${directory}/src/beta-cuda/CMakeLists.txt"
  [=[set(SRC_DIR "${CMAKE_CURRENT_LIST_DIR}/../aux-omp")
UNZIPFILE("${CMAKE_CURRENT_LIST_DIR}/data.tar.gz")
UNZIPFILE("${SRC_DIR}/refdata.tar.gz")]=])
write("${directory}/src/beta-cuda/data.tar.gz.dvc" "outs:\n- md5: 00112233445566778899aabbccddeeff\n  size: 1234\n  hash: md5\n  path: data.tar.gz\n")
write("${directory}/src/aux-omp/refdata.tar.gz.dvc" "outs:\n- md5: 33333333333333333333333333333333\n  size: 77\n  hash: md5\n  path: refdata.tar.gz\n")
write("${directory}/src/gamma-cuda/CMakeLists.txt" "")
write("${directory}/src/gamma-cuda/LICENSE" "GNU GENERAL PUBLIC LICENSE\nVersion 3, 29 June 2007\n")
write("${directory}/src/delta-cuda/CMakeLists.txt"
  [=[UNZIPFILE("${CMAKE_CURRENT_LIST_DIR}/../data/shared/input.tar.bz")]=])
write("${directory}/src/delta-cuda/LICENSE" "Creative Commons license by-nc 3.0.\n")
write("${directory}/src/data/shared/input.tar.bz.dvc" "outs:\n- md5: ffeeddccbbaa99887766554433221100\n  size: 987\n  hash: md5\n  path: input.tar.bz\n")
write("${directory}/src/epsilon-cuda/CMakeLists.txt" "")
write("${directory}/src/epsilon-cuda/LICENSE" "A licence this build does not recognise.\n")
write("${directory}/src/zeta-cuda/CMakeLists.txt" "")
write("${directory}/src/zeta-cuda/include/LICENSE" "Apache License\nVersion 2.0, January 2004\n")
write("${directory}/src/eta-cuda/CMakeLists.txt" "")
write("${directory}/src/eta-cuda/LICENSE" "GNU GENERAL PUBLIC LICENSE\nVersion 2, June 1991\n")
write("${directory}/src/eta-cuda/COPYING" "MIT License\nPermission is hereby granted, free of charge.\n")
write("${directory}/src/theta-cuda/CMakeLists.txt" "")
write("${directory}/src/theta-hip/LICENSE" "GNU LESSER GENERAL PUBLIC LICENSE\nVersion 3, 29 June 2007\n")
write("${directory}/src/unused-cuda/skip.tar.gz.dvc" "outs:\n- md5: 11111111111111111111111111111111\n  size: 1\n  hash: md5\n  path: skip.tar.gz\n")
write("${directory}/src/orphan-omp/orphan.tar.gz.dvc" "outs:\n- md5: 22222222222222222222222222222222\n  size: 2\n  hash: md5\n  path: orphan.tar.gz\n")

hecbench_dvc_entries("${directory}" entries)
set(expected
  "00112233445566778899aabbccddeeff|1234|src/beta-cuda/data.tar.gz"
  "11111111111111111111111111111111|1|src/unused-cuda/skip.tar.gz"
  "33333333333333333333333333333333|77|src/aux-omp/refdata.tar.gz"
  "ffeeddccbbaa99887766554433221100|987|src/data/shared/input.tar.bz")
list(SORT expected)
if(NOT entries STREQUAL expected)
  message(FATAL_ERROR "DVC derivation changed: ${entries}")
endif()
hecbench_dvc_url("00112233445566778899aabbccddeeff" url)
if(NOT url STREQUAL "https://hecbench-geekdude.s3.amazonaws.com/files/md5/00/112233445566778899aabbccddeeff")
  message(FATAL_ERROR "DVC URL derivation changed: ${url}")
endif()

function(expect_feature feature expected)
  hecbench_cuda_feature_supported("${feature}" actual)
  if(NOT actual STREQUAL expected)
    message(FATAL_ERROR "CUDA feature support changed: ${feature} expected ${expected}, got ${actual}")
  endif()
endfunction()
set(CMAKE_CUDA17_STANDARD_COMPILE_OPTION "-std=c++17")
unset(CMAKE_CUDA20_STANDARD_COMPILE_OPTION)
expect_feature(cuda_std_17 TRUE)
expect_feature(cuda_std_20 FALSE)
expect_feature(cuda_std_bogus FALSE)

function(expect_unsupported content expected)
  hecbench_unsupported_cuda_features("${content}" actual)
  if(NOT actual STREQUAL expected)
    message(FATAL_ERROR "unsupported CUDA features changed: expected [${expected}], got [${actual}]")
  endif()
endfunction()
expect_unsupported("target_compile_features(ssm-cuda PRIVATE cuda_std_20)" "cuda_std_20")
expect_unsupported("set(CMAKE_CUDA_STANDARD 17)" "")
expect_unsupported("cuda_std_20 cuda_std_20" "cuda_std_20")
set(CMAKE_CUDA20_STANDARD_COMPILE_OPTION "-std=c++20")
expect_feature(cuda_std_20 TRUE)
expect_unsupported("target_compile_features(ssm-cuda PRIVATE cuda_std_20)" "")

function(expect_class text name class)
  hecbench_classify_licence_text("${text}" actual_name actual_class)
  if(NOT actual_name STREQUAL name OR NOT actual_class STREQUAL class)
    message(FATAL_ERROR "licence classification changed: expected ${name}/${class}, got ${actual_name}/${actual_class}")
  endif()
endfunction()
expect_class("Permission is hereby granted, free of charge, to any person" MIT permissive)
expect_class("Redistribution and use in source and binary forms, with or without modification.  Neither the name of the copyright holder"
  BSD-3-Clause permissive)
expect_class("Redistribution and use in source and binary forms, with or without modification." BSD-2-Clause permissive)
expect_class("GNU LESSER GENERAL PUBLIC LICENSE\nVersion 2.1, February 1999" LGPL-2.1 copyleft)
expect_class("GNU AFFERO GENERAL PUBLIC LICENSE\nVersion 3" unknown unknown)
expect_class("Copyright 1993-2012 NVIDIA Corporation.  All rights reserved.\nNOTICE TO LICENSEE:\nThese Licensed Deliverables are PROPRIETARY and CONFIDENTIAL to NVIDIA."
  "NVIDIA proprietary" proprietary)
expect_class("GNU LESSER GENERAL PUBLIC LICENSE\nVersion 2.1\nrestrictions of other proprietary libraries" LGPL-2.1 copyleft)
expect_class("Creative Commons license by-nc 3.0." CC-BY-NC-3.0 non-commercial)
expect_class("Dhara Modeling Software\nNon-Exclusive, Non-Commercial Use License" "Dhara non-commercial" non-commercial)
expect_class("Redistribution  and  use in  source and binary forms, with or without modification" BSD-2-Clause permissive)

set(diagnostic "/usr/local/cuda/targets/x86_64-linux/include/cuda_fp16.hpp(988): error: identifier \"__half\" is undefined")
hecbench_failure_line("gemm|${diagnostic}" "${directory}/source" "${directory}/build" line)
set(expected "gemm: /<cuda>/targets/x86_64-linux/include/cuda_fp16.hpp(988): error: identifier \"__half\" is undefined")
if(NOT line STREQUAL expected)
  message(FATAL_ERROR "failure report is not relocatable: ${line}")
endif()

hecbench_failure_line("gemm|/work/build/hecbench/source/src/gemm-cuda/main.cu(9): error: operand types are incompatible"
  "${directory}/source" "${directory}/build" line)
conformance_relocation_roots(roots)
foreach(root IN LISTS roots)
  if(line MATCHES "${root}")
    message(FATAL_ERROR "failure report retains ${root}: ${line}")
  endif()
endforeach()

hecbench_licence_classification("${directory}" rows)
set(expected
  "alpha|MIT|permissive|src/alpha-cuda/LICENSE"
  "beta|BSD-3-Clause|permissive|"
  "delta|CC-BY-NC-3.0|non-commercial|src/delta-cuda/LICENSE"
  "epsilon|unknown|unknown|src/epsilon-cuda/LICENSE"
  "eta|GPL-2.0+MIT|copyleft|src/eta-cuda/COPYING+src/eta-cuda/LICENSE"
  "gamma|GPL-3.0|copyleft|src/gamma-cuda/LICENSE"
  "theta|LGPL-3.0|copyleft|src/theta-hip/LICENSE"
  "zeta|Apache-2.0|permissive|src/zeta-cuda/include/LICENSE")
list(SORT expected)
if(NOT rows STREQUAL expected)
  message(FATAL_ERROR "licence classification changed: ${rows}")
endif()

write("${directory}/benchmarks.yaml" [=[
accuracy:
  categories: [algorithms]
  test:
    regex: 'result'
    args: ["8192", "10000"]
ace:
  test:
    args: ["100"]
empty_test:
  test:
    args: []
no_test:
  categories: [ml]
slash/name:
  test:
    args: ["1", "2"]
]=])
hecbench_benchmark_arguments("${directory}/benchmarks.yaml" yaml_args)
set(expected_args
  "{\n  \"hecbench_accuracy\": [\"8192\", \"10000\"],\n  \"hecbench_ace\": [\"100\"],\n  \"hecbench_slash_name\": [\"1\", \"2\"]\n}")
if(NOT yaml_args STREQUAL expected_args)
  message(FATAL_ERROR "benchmark arguments changed: expected [${expected_args}], got [${yaml_args}]")
endif()

file(REMOVE_RECURSE "${directory}")
