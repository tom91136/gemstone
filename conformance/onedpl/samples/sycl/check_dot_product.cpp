#pragma region case: onedpl-dot-product
#pragma region do: {dpcpp} -fsycl -std=c++20 -O2 -I{onedpl_include} -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass

// -*- C++ -*-
// Adapted from oneDPL examples/dot_product/src/main.cpp.
// Copyright (C) Intel Corporation
// SPDX-License-Identifier: Apache-2.0 WITH LLVM-exception
// See ../LICENSE-ONEDPL-APACHE-LLVM.

#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>
#include <oneapi/dpl/numeric>

#include <cassert>
#include <cstdio>

int main()
{
    constexpr std::size_t size = 257;
    auto policy = oneapi::dpl::execution::dpcpp_default;
    auto* left = sycl::malloc_shared<double>(size, policy.queue());
    auto* right = sycl::malloc_shared<double>(size, policy.queue());
    assert(left != nullptr && right != nullptr);
    for(std::size_t index = 0; index < size; ++index)
    {
        left[index] = static_cast<double>(index);
        right[index] = 2.0;
    }
    const double result = std::transform_reduce(policy, left, left + size, right, 0.0);
    assert(result == static_cast<double>((size - 1) * size));
    sycl::free(right, policy.queue());
    sycl::free(left, policy.queue());
    std::puts("pass");
    return 0;
}
