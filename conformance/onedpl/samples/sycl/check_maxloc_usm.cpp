#pragma region case: onedpl-maxloc-usm
#pragma region do: {dpcpp} -fsycl -std=c++20 -O2 -I{onedpl_include} -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass

// Adapted from oneDPL examples/maxloc_reductions/maxloc_usm.cpp.
// Copyright Intel Corporation
// SPDX-License-Identifier: MIT

#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>

#include <cassert>
#include <cstdio>

int main()
{
    auto policy = oneapi::dpl::execution::dpcpp_default;
    constexpr std::size_t size = 7;
    auto* data = sycl::malloc_shared<int>(size, policy.queue());
    assert(data != nullptr);
    const int input[size] = {2, 2, 2, 4, 1, 1, 1};
    for(std::size_t index = 0; index < size; ++index)
        data[index] = input[index];
    const auto maximum = oneapi::dpl::max_element(policy, data, data + size);
    assert(maximum == data + 3);
    assert(*maximum == 4);
    sycl::free(data, policy.queue());
    std::puts("pass");
    return 0;
}
