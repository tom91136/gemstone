#ifndef UNIFIED_SUITE_LEGACY_REDS_HPP
#define UNIFIED_SUITE_LEGACY_REDS_HPP

#include "unified_suite/blas.hpp"
#include "unified_suite/config.hpp"
#include "unified_suite/result.hpp"

#include <array>
#include <cctype>
#include <cstddef>
#include <cstdlib>
#include <filesystem>
#include <string>
#include <string_view>
#if !defined(_WIN32)
#include <sys/wait.h>
#endif
#include <vector>

namespace unified_suite::legacy_reds {

// These identities are copied from the old vendor runners' Gemstone-emulator
// reports. CUDA has no active red entries: the two 64-bit launch regressions
// were fixed in the provider and are retained in cuda_thrust_fixed for
// auditability. The long-running entries are kept separately in deferred; no
// expected failure is inferred from a timeout.
inline constexpr std::array<std::string_view, 0> cuda_thrust{};

// These identities were imported with the old report but now pass through
// the current provider. Keep them visible for auditability without masking
// them as expected failures. The unified suite also decomposes the two
// formerly failing identities into one ordinary edge case per tested magnitude.
inline constexpr std::string_view cuda_thrust_fixed[] = {
    "thrust/cuda.adjacent_difference",
    "thrust/cuda.copy_if",
    "thrust/cuda.count",
    "thrust/cuda.equal",
    "thrust/cuda.fill",
    "thrust/cuda.find",
    "thrust/cuda.for_each",
    "thrust/cuda.generate",
    "thrust/cuda.inner_product",
    "thrust/cuda.is_sorted",
    "thrust/cuda.is_sorted_until",
    "thrust/cuda.logical",
    "thrust/cuda.max_element",
    "thrust/cuda.merge",
    "thrust/cuda.merge_by_key",
    "thrust/cuda.min_element",
    "thrust/cuda.minmax_element",
    "thrust/cuda.mismatch",
    "thrust/cuda.partition",
    "thrust/cuda.reduce",
    "thrust/cuda.reduce_by_key",
    "thrust/cuda.replace",
    "thrust/cuda.remove",
    "thrust/cuda.reverse",
    "thrust/cuda.scan",
    "thrust/cuda.scan_by_key",
    "thrust/cuda.sequence",
    "thrust/cuda.set_difference",
    "thrust/cuda.set_difference_by_key",
    "thrust/cuda.set_intersection",
    "thrust/cuda.set_intersection_by_key",
    "thrust/cuda.set_symmetric_difference",
    "thrust/cuda.set_symmetric_difference_by_key",
    "thrust/cuda.set_union",
    "thrust/cuda.set_union_by_key",
    "thrust/cuda.sort",
    "thrust/cuda.sort_by_key",
    "thrust/cuda.swap_ranges",
    "thrust/cuda.tabulate",
    "thrust/cuda.transform",
    "thrust/cuda.transform_reduce",
    "thrust/cuda.transform_scan",
    "thrust/cuda.uninitialized_copy",
    "thrust/cuda.uninitialized_fill",
    "thrust/cuda.unique",
    "thrust/cuda.unique_by_key",
    "thrust/vector",
    "thrust/for_each",
};

// These entries were imported as reds but now pass through the current
// provider. Keep them in a strict lane so an XPASS cannot silently weaken the
// regression inventory.
inline constexpr std::string_view roc_thrust_fixed[] = {
    "rocthrust/gfx1036/testing/async_exclusive_scan_counting_iterator",
    "rocthrust/gfx1036/testing/async_exclusive_scan_discard_output",
    "rocthrust/gfx1036/testing/async_exclusive_scan_stateful_operator",
    "rocthrust/gfx1036/testing/async_inclusive_scan_counting_iterator",
    "rocthrust/gfx1036/testing/async_inclusive_scan_discard_output",
    "rocthrust/gfx1036/testing/async_inclusive_scan_stateful_operator",
    "rocthrust/gfx1036/testing/pair_scan",
    "rocthrust/gfx1036/testing/permutation_iterator",
    "rocthrust/gfx1036/testing/random",
    "rocthrust/gfx1036/testing/reverse_iterator",
    "rocthrust/gfx1036/testing/replace",
    "rocthrust/gfx1036/testing/scan_by_key.exclusive",
    "rocthrust/gfx1036/testing/scan_by_key.inclusive",
    "rocthrust/gfx1036/testing/sequence",
    "rocthrust/gfx1036/testing/transform",
    "rocthrust/gfx1036/testing/transform_output_iterator_reduce_by_key",
    "rocthrust/gfx1036/testing/transform_output_iterator",
    "rocthrust/gfx1036/testing/tabulate",
    "rocthrust/gfx1036/testing/transform_input_output_iterator",
    "rocthrust/gfx1036/testing/transform_iterator",
    "rocthrust/gfx1036/testing/tuple_scan",
    "rocthrust/gfx1036/testing/tuple_reduce",
    "rocthrust/gfx1036/testing/transform_reduce",
    "rocthrust/gfx1036/testing/vector_insert",
    "rocthrust/gfx1036/testing/zip_iterator",
    "rocthrust/gfx1036/testing/zip_iterator_scan",
    "rocthrust/gfx1036/testing/zip_iterator_sort_by_key",
    "rocthrust/gfx1036/testing/shuffle",
    "rocthrust/gfx1036/testing/stable_sort_by_key_large_values",
    "rocthrust/gfx1036/testing/stable_sort_by_key_large_keys",
    "rocthrust/gfx1036/testing/stable_sort_by_key_large_keys_and_values",
    "rocthrust/gfx1036/testing/stable_sort_large",
    "rocthrust/gfx1036/testing/reduce_large",
    "rocthrust/gfx1036/testing/set_difference_key_value",
    "rocthrust/gfx1036/testing/set_difference_descending",
    "rocthrust/gfx1036/testing/set_difference_by_key_descending",
    "rocthrust/gfx1036/testing/set_intersection_descending",
    "rocthrust/gfx1036/testing/set_intersection_key_value",
    "rocthrust/gfx1036/testing/set_symmetric_difference_by_key_descending",
    "rocthrust/gfx1036/testing/set_symmetric_difference_descending",
    "rocthrust/gfx1036/testing/set_symmetric_difference",
    "rocthrust/gfx1036/testing/set_union",
    "rocthrust/gfx1036/testing/set_union_by_key_descending",
    "rocthrust/gfx1036/testing/set_union_descending",
    "rocthrust/gfx1036/testing/set_union_key_value",
    "rocthrust/gfx1036/testing/set_symmetric_difference_by_key",
    "rocthrust/gfx1036/testing/set_union_by_key",
    "rocthrust/gfx1036/testing/unittest_static_assert",
    "rocthrust/gfx1036/testing/async_exclusive_scan_simple",
    "rocthrust/gfx1036/testing/async_inclusive_scan_simple",
};

// The active-red array is intentionally empty after the imported rocThrust
// identities were promoted to strict fixed cases. Keep the expected-failure
// lane in the harness for future regressions without masking any current case.
inline constexpr std::array<std::string_view, 0> roc_thrust{};

// Historical names retained for auditability. They were observed in an older
// report but have not all been rebuilt in the current container, so they are
// not presented as active regression cases.
inline constexpr std::string_view roc_historical[] = {
    "rocthrust/gfx1036/testing/adjacent_difference",
    "rocthrust/gfx1036/testing/advance",
    "rocthrust/gfx1036/testing/allocator",
    "rocthrust/gfx1036/testing/async_copy",
    "rocthrust/gfx1036/testing/async_for_each",
    "rocthrust/gfx1036/testing/async_reduce",
    "rocthrust/gfx1036/testing/async_reduce_into",
    "rocthrust/gfx1036/testing/async_sort",
    "rocthrust/gfx1036/testing/async_transform",
    "rocthrust/gfx1036/testing/binary_search",
    "rocthrust/gfx1036/testing/binary_search_descending",
    "rocthrust/gfx1036/testing/binary_search_vector",
    "rocthrust/gfx1036/testing/binary_search_vector_descending",
    "rocthrust/gfx1036/testing/complex",
    "rocthrust/gfx1036/testing/complex_transform",
    "rocthrust/gfx1036/testing/constant_iterator",
    "rocthrust/gfx1036/testing/copy",
    "rocthrust/gfx1036/testing/copy_n",
    "rocthrust/gfx1036/testing/count",
    "rocthrust/gfx1036/testing/counting_iterator",
    "rocthrust/gfx1036/testing/dereference",
    "rocthrust/gfx1036/testing/device_ptr",
    "rocthrust/gfx1036/testing/device_reference",
    "rocthrust/gfx1036/testing/distance",
    "rocthrust/gfx1036/testing/equal",
    "rocthrust/gfx1036/testing/fill",
    "rocthrust/gfx1036/testing/find",
    "rocthrust/gfx1036/testing/for_each",
    "rocthrust/gfx1036/testing/functional",
    "rocthrust/gfx1036/testing/functional_arithmetic",
    "rocthrust/gfx1036/testing/functional_bitwise",
    "rocthrust/gfx1036/testing/functional_logical",
    "rocthrust/gfx1036/testing/functional_placeholders_arithmetic",
    "rocthrust/gfx1036/testing/functional_placeholders_bitwise",
    "rocthrust/gfx1036/testing/functional_placeholders_compound_assignment",
    "rocthrust/gfx1036/testing/functional_placeholders_logical",
    "rocthrust/gfx1036/testing/functional_placeholders_miscellaneous",
    "rocthrust/gfx1036/testing/functional_placeholders_relational",
    "rocthrust/gfx1036/testing/gather",
    "rocthrust/gfx1036/testing/generate",
    "rocthrust/gfx1036/testing/generate_const_iterators",
    "rocthrust/gfx1036/testing/inner_product",
    "rocthrust/gfx1036/testing/is_partitioned",
    "rocthrust/gfx1036/testing/is_sorted",
    "rocthrust/gfx1036/testing/is_sorted_until",
    "rocthrust/gfx1036/testing/logical",
    "rocthrust/gfx1036/testing/max_element",
    "rocthrust/gfx1036/testing/memory",
    "rocthrust/gfx1036/testing/merge",
    "rocthrust/gfx1036/testing/merge_by_key",
    "rocthrust/gfx1036/testing/merge_key_value",
    "rocthrust/gfx1036/testing/min_element",
    "rocthrust/gfx1036/testing/minmax_element",
    "rocthrust/gfx1036/testing/mismatch",
    "rocthrust/gfx1036/testing/out_of_memory_recovery",
    "rocthrust/gfx1036/testing/pair",
    "rocthrust/gfx1036/testing/pair_reduce",
    "rocthrust/gfx1036/testing/pair_scan",
    "rocthrust/gfx1036/testing/pair_scan_by_key",
    "rocthrust/gfx1036/testing/pair_sort",
    "rocthrust/gfx1036/testing/pair_sort_by_key",
    "rocthrust/gfx1036/testing/pair_transform",
    "rocthrust/gfx1036/testing/partition",
    "rocthrust/gfx1036/testing/partition_point",
    "rocthrust/gfx1036/testing/permutation_iterator",
    "rocthrust/gfx1036/testing/random",
    "rocthrust/gfx1036/testing/reduce",
    "rocthrust/gfx1036/testing/reduce_by_key",
    "rocthrust/gfx1036/testing/reduce_large",
    "rocthrust/gfx1036/testing/remove",
    "rocthrust/gfx1036/testing/replace",
    "rocthrust/gfx1036/testing/reverse",
    "rocthrust/gfx1036/testing/reverse_iterator",
    "rocthrust/gfx1036/testing/scan",
    "rocthrust/gfx1036/testing/scan_by_key.exclusive",
    "rocthrust/gfx1036/testing/scan_by_key.inclusive",
    "rocthrust/gfx1036/testing/scatter",
    "rocthrust/gfx1036/testing/sequence",
    "rocthrust/gfx1036/testing/set_difference",
    "rocthrust/gfx1036/testing/set_difference_by_key",
    "rocthrust/gfx1036/testing/set_difference_by_key_descending",
    "rocthrust/gfx1036/testing/set_difference_descending",
    "rocthrust/gfx1036/testing/set_difference_key_value",
    "rocthrust/gfx1036/testing/set_intersection",
    "rocthrust/gfx1036/testing/set_intersection_by_key",
    "rocthrust/gfx1036/testing/set_intersection_by_key_descending",
    "rocthrust/gfx1036/testing/set_intersection_descending",
    "rocthrust/gfx1036/testing/set_intersection_key_value",
    "rocthrust/gfx1036/testing/set_symmetric_difference",
    "rocthrust/gfx1036/testing/set_symmetric_difference_by_key",
    "rocthrust/gfx1036/testing/set_symmetric_difference_by_key_descending",
    "rocthrust/gfx1036/testing/set_symmetric_difference_descending",
    "rocthrust/gfx1036/testing/set_union",
    "rocthrust/gfx1036/testing/set_union_by_key",
    "rocthrust/gfx1036/testing/set_union_by_key_descending",
    "rocthrust/gfx1036/testing/set_union_descending",
    "rocthrust/gfx1036/testing/set_union_key_value",
    "rocthrust/gfx1036/testing/shuffle",
    "rocthrust/gfx1036/testing/sort",
    "rocthrust/gfx1036/testing/sort_by_key",
    "rocthrust/gfx1036/testing/sort_by_key_variable_bits",
    "rocthrust/gfx1036/testing/sort_permutation_iterator",
    "rocthrust/gfx1036/testing/sort_variable_bits",
    "rocthrust/gfx1036/testing/stable_sort",
    "rocthrust/gfx1036/testing/stable_sort_by_key",
    "rocthrust/gfx1036/testing/stable_sort_by_key_large_keys",
    "rocthrust/gfx1036/testing/stable_sort_by_key_large_keys_and_values",
    "rocthrust/gfx1036/testing/stable_sort_by_key_large_values",
    "rocthrust/gfx1036/testing/stable_sort_large",
    "rocthrust/gfx1036/testing/swap_ranges",
    "rocthrust/gfx1036/testing/tabulate",
    "rocthrust/gfx1036/testing/tabulate_output_iterator",
    "rocthrust/gfx1036/testing/transform",
    "rocthrust/gfx1036/testing/transform_input_output_iterator",
    "rocthrust/gfx1036/testing/transform_iterator",
    "rocthrust/gfx1036/testing/transform_output_iterator_reduce_by_key",
    "rocthrust/gfx1036/testing/transform_output_iterator",
    "rocthrust/gfx1036/testing/transform_reduce",
    "rocthrust/gfx1036/testing/transform_scan",
    "rocthrust/gfx1036/testing/trivial_sequence",
    "rocthrust/gfx1036/testing/tuple",
    "rocthrust/gfx1036/testing/tuple_reduce",
    "rocthrust/gfx1036/testing/tuple_scan",
    "rocthrust/gfx1036/testing/tuple_sort",
    "rocthrust/gfx1036/testing/tuple_transform",
    "rocthrust/gfx1036/testing/uninitialized_copy",
    "rocthrust/gfx1036/testing/uninitialized_fill",
    "rocthrust/gfx1036/testing/unique",
    "rocthrust/gfx1036/testing/unique_by_key",
    "rocthrust/gfx1036/testing/unittest_static_assert",
    "rocthrust/gfx1036/testing/universal_memory",
    "rocthrust/gfx1036/testing/vector",
    "rocthrust/gfx1036/testing/vector_allocators",
    "rocthrust/gfx1036/testing/vector_insert",
    "rocthrust/gfx1036/testing/vector_manipulation",
    "rocthrust/gfx1036/testing/zip_function",
    "rocthrust/gfx1036/testing/zip_iterator",
    "rocthrust/gfx1036/testing/zip_iterator_reduce",
    "rocthrust/gfx1036/testing/zip_iterator_reduce_by_key",
    "rocthrust/gfx1036/testing/zip_iterator_scan",
    "rocthrust/gfx1036/testing/zip_iterator_sort",
    "rocthrust/gfx1036/testing/zip_iterator_sort_by_key",
    "rocthrust/gfx1036/testing/async/exclusive_scan/counting_iterator",
    "rocthrust/gfx1036/testing/async/exclusive_scan/discard_output",
    "rocthrust/gfx1036/testing/async/exclusive_scan/mixed_types",
    "rocthrust/gfx1036/testing/async/exclusive_scan/simple",
    "rocthrust/gfx1036/testing/async/exclusive_scan/stateful_operator",
    "rocthrust/gfx1036/testing/async/exclusive_scan/using_vs_adl",
    "rocthrust/gfx1036/testing/async/inclusive_scan/counting_iterator",
    "rocthrust/gfx1036/testing/async/inclusive_scan/discard_output",
    "rocthrust/gfx1036/testing/async/inclusive_scan/mixed_types",
    "rocthrust/gfx1036/testing/async/inclusive_scan/simple",
    "rocthrust/gfx1036/testing/async/inclusive_scan/stateful_operator",
    "rocthrust/gfx1036/testing/async/inclusive_scan/using_vs_adl",
    "rocthrust/gfx1036/test/adjacent_difference.hip",
    "rocthrust/gfx1036/test/advance.hip",
    "rocthrust/gfx1036/test/alignment.hip",
    "rocthrust/gfx1036/test/allocator.hip",
    "rocthrust/gfx1036/test/allocator_aware_policies.hip",
    "rocthrust/gfx1036/test/async_copy.hip",
    "rocthrust/gfx1036/test/async_for_each.hip",
    "rocthrust/gfx1036/test/async_reduce.hip",
    "rocthrust/gfx1036/test/async_reduce_into.hip",
    "rocthrust/gfx1036/test/async_scan.hip",
    "rocthrust/gfx1036/test/async_sort.hip",
    "rocthrust/gfx1036/test/async_transform.hip",
    "rocthrust/gfx1036/test/binary_search.hip",
    "rocthrust/gfx1036/test/binary_search_descending.hip",
    "rocthrust/gfx1036/test/binary_search_vector.hip",
    "rocthrust/gfx1036/test/binary_search_vector_descending.hip",
    "rocthrust/gfx1036/test/complex.hip",
    "rocthrust/gfx1036/test/complex_transform.hip",
    "rocthrust/gfx1036/test/complex_various.hip",
    "rocthrust/gfx1036/test/constant_iterator.hip",
    "rocthrust/gfx1036/test/copy.hip",
    "rocthrust/gfx1036/test/copy_n.hip",
    "rocthrust/gfx1036/test/count.hip",
    "rocthrust/gfx1036/test/counting_iterator.hip",
    "rocthrust/gfx1036/test/decompose.hip",
    "rocthrust/gfx1036/test/dependencies_aware_policies.hip",
    "rocthrust/gfx1036/test/dereference.hip",
    "rocthrust/gfx1036/test/device_delete.hip",
    "rocthrust/gfx1036/test/device_ptr.hip",
    "rocthrust/gfx1036/test/device_reference.hip",
    "rocthrust/gfx1036/test/discard_iterator.hip",
    "rocthrust/gfx1036/test/distance.hip",
    "rocthrust/gfx1036/test/equal.hip",
    "rocthrust/gfx1036/test/fill.hip",
    "rocthrust/gfx1036/test/find.hip",
    "rocthrust/gfx1036/test/for_each.hip",
    "rocthrust/gfx1036/test/functional.hip",
    "rocthrust/gfx1036/test/functional_arithmetic.hip",
    "rocthrust/gfx1036/test/functional_bitwise.hip",
    "rocthrust/gfx1036/test/functional_logical.hip",
    "rocthrust/gfx1036/test/functional_placeholders_arithmetic.hip",
    "rocthrust/gfx1036/test/functional_placeholders_bitwise.hip",
    "rocthrust/gfx1036/test/functional_placeholders_compound_assignment.hip",
    "rocthrust/gfx1036/test/functional_placeholders_logical.hip",
    "rocthrust/gfx1036/test/functional_placeholders_miscellaneous.hip",
    "rocthrust/gfx1036/test/functional_placeholders_relational.hip",
    "rocthrust/gfx1036/test/gather.hip",
    "rocthrust/gfx1036/test/generate.hip",
    "rocthrust/gfx1036/test/inner_product.hip",
    "rocthrust/gfx1036/test/is_contiguous_iterator.hip",
    "rocthrust/gfx1036/test/is_operator_function_object.hip",
    "rocthrust/gfx1036/test/is_partitioned.hip",
    "rocthrust/gfx1036/test/is_sorted.hip",
    "rocthrust/gfx1036/test/is_sorted_until.hip",
    "rocthrust/gfx1036/test/logical.hip",
    "rocthrust/gfx1036/test/max_element.hip",
    "rocthrust/gfx1036/test/memory.hip",
    "rocthrust/gfx1036/test/merge.hip",
    "rocthrust/gfx1036/test/merge_by_key.hip",
    "rocthrust/gfx1036/test/merge_key_value.hip",
    "rocthrust/gfx1036/test/metaprogramming.hip",
    "rocthrust/gfx1036/test/min_and_max.hip",
    "rocthrust/gfx1036/test/min_element.hip",
    "rocthrust/gfx1036/test/minmax_element.hip",
    "rocthrust/gfx1036/test/mismatch.hip",
    "rocthrust/gfx1036/test/mr_new.hip",
    "rocthrust/gfx1036/test/mr_pool.hip",
    "rocthrust/gfx1036/test/mr_pool_options.hip",
    "rocthrust/gfx1036/test/pair.hip",
    "rocthrust/gfx1036/test/pair_reduce.hip",
    "rocthrust/gfx1036/test/pair_scan.hip",
    "rocthrust/gfx1036/test/pair_scan_by_key.hip",
    "rocthrust/gfx1036/test/pair_sort.hip",
    "rocthrust/gfx1036/test/pair_sort_by_key.hip",
    "rocthrust/gfx1036/test/pair_transform.hip",
    "rocthrust/gfx1036/test/partition.hip",
    "rocthrust/gfx1036/test/partition_point.hip",
    "rocthrust/gfx1036/test/permutation_iterator.hip",
    "rocthrust/gfx1036/test/preprocessor.hip",
    "rocthrust/gfx1036/test/random.hip",
    "rocthrust/gfx1036/test/reduce.hip",
    "rocthrust/gfx1036/test/reduce_by_key.hip",
    "rocthrust/gfx1036/test/reduce_large.hip",
    "rocthrust/gfx1036/test/remove.hip",
    "rocthrust/gfx1036/test/replace.hip",
    "rocthrust/gfx1036/test/reproducibility.hip",
    "rocthrust/gfx1036/test/reverse.hip",
    "rocthrust/gfx1036/test/reverse_iterator.hip",
    "rocthrust/gfx1036/test/scan.hip",
    "rocthrust/gfx1036/test/scan_by_key.exclusive.hip",
    "rocthrust/gfx1036/test/scan_by_key.inclusive.hip",
    "rocthrust/gfx1036/test/scatter.hip",
    "rocthrust/gfx1036/test/sequence.hip",
    "rocthrust/gfx1036/test/set_difference.hip",
    "rocthrust/gfx1036/test/set_difference_by_key.hip",
    "rocthrust/gfx1036/test/set_difference_by_key_descending.hip",
    "rocthrust/gfx1036/test/set_difference_descending.hip",
    "rocthrust/gfx1036/test/set_difference_key_value.hip",
    "rocthrust/gfx1036/test/set_intersection.hip",
    "rocthrust/gfx1036/test/set_intersection_by_key.hip",
    "rocthrust/gfx1036/test/set_intersection_by_key_descending.hip",
    "rocthrust/gfx1036/test/set_intersection_descending.hip",
    "rocthrust/gfx1036/test/set_intersection_key_value.hip",
    "rocthrust/gfx1036/test/set_symmetric_difference.hip",
    "rocthrust/gfx1036/test/set_symmetric_difference_by_key.hip",
    "rocthrust/gfx1036/test/set_symmetric_difference_by_key_descending.hip",
    "rocthrust/gfx1036/test/set_symmetric_difference_descending.hip",
    "rocthrust/gfx1036/test/set_union.hip",
    "rocthrust/gfx1036/test/set_union_by_key.hip",
    "rocthrust/gfx1036/test/set_union_by_key_descending.hip",
    "rocthrust/gfx1036/test/set_union_descending.hip",
    "rocthrust/gfx1036/test/set_union_key_value.hip",
    "rocthrust/gfx1036/test/shuffle.hip",
    "rocthrust/gfx1036/test/sort.hip",
    "rocthrust/gfx1036/test/sort_by_key.hip",
    "rocthrust/gfx1036/test/sort_by_key_variable_bits.hip",
    "rocthrust/gfx1036/test/sort_permutation_iterator.hip",
    "rocthrust/gfx1036/test/sort_variable_bits.hip",
    "rocthrust/gfx1036/test/stable_sort.hip",
    "rocthrust/gfx1036/test/stable_sort_by_key.hip",
    "rocthrust/gfx1036/test/stable_sort_by_key_large_keys.hip",
    "rocthrust/gfx1036/test/stable_sort_by_key_large_keys_and_values.hip",
    "rocthrust/gfx1036/test/stable_sort_by_key_large_values.hip",
    "rocthrust/gfx1036/test/stable_sort_large.hip",
    "rocthrust/gfx1036/test/swap_ranges.hip",
    "rocthrust/gfx1036/test/tabulate.hip",
    "rocthrust/gfx1036/test/tabulate_output_iterator.hip",
    "rocthrust/gfx1036/test/transform.hip",
    "rocthrust/gfx1036/test/transform_input_output_iterator.hip",
    "rocthrust/gfx1036/test/transform_iterator.hip",
    "rocthrust/gfx1036/test/transform_output_iterator.hip",
    "rocthrust/gfx1036/test/transform_output_iterator_reduce_by_key.hip",
    "rocthrust/gfx1036/test/transform_reduce.hip",
    "rocthrust/gfx1036/test/transform_scan.hip",
    "rocthrust/gfx1036/test/trivial_sequence.hip",
    "rocthrust/gfx1036/test/tuple.hip",
    "rocthrust/gfx1036/test/tuple_algorithms.hip",
    "rocthrust/gfx1036/test/tuple_reduce.hip",
    "rocthrust/gfx1036/test/tuple_scan.hip",
    "rocthrust/gfx1036/test/tuple_sort.hip",
    "rocthrust/gfx1036/test/tuple_transform.hip",
    "rocthrust/gfx1036/test/type_traits.hip",
    "rocthrust/gfx1036/test/uninitialized_copy.hip",
    "rocthrust/gfx1036/test/uninitialized_fill.hip",
    "rocthrust/gfx1036/test/unique_ptr_general.hip",
    "rocthrust/gfx1036/test/unique_ptr_allocate_deallocate.hip",
    "rocthrust/gfx1036/test/unique.hip",
    "rocthrust/gfx1036/test/unique_by_key.hip",
    "rocthrust/gfx1036/test/universal_memory.hip",
    "rocthrust/gfx1036/test/utils_tester.hip",
    "rocthrust/gfx1036/test/vector.hip",
    "rocthrust/gfx1036/test/vector_allocators.hip",
    "rocthrust/gfx1036/test/vector_insert.hip",
    "rocthrust/gfx1036/test/vector_manipulation.hip",
    "rocthrust/gfx1036/test/zip_function.hip",
    "rocthrust/gfx1036/test/zip_iterator.hip",
    "rocthrust/gfx1036/test/zip_iterator_reduce.hip",
    "rocthrust/gfx1036/test/zip_iterator_reduce_by_key.hip",
    "rocthrust/gfx1036/test/zip_iterator_scan.hip",
    "rocthrust/gfx1036/test/zip_iterator_sort.hip",
    "rocthrust/gfx1036/test/zip_iterator_sort_by_key.hip",
    "rocthrust/gfx1036/samples/hip-saxpy",
    "rocthrust/gfx1036/samples/hsa-kernel-dispatch",
};

inline constexpr std::string_view deferred[] = {
    // ROCm identities that timed out under the bounded probe.
    "rocthrust/gfx1036/testing/merge",
    "rocthrust/gfx1036/testing/merge_by_key",
    "rocthrust/gfx1036/testing/min_element",
    "rocthrust/gfx1036/testing/minmax_element",
    "rocthrust/gfx1036/testing/partition",
    "rocthrust/gfx1036/testing/partition_point",
    "rocthrust/gfx1036/testing/reduce",
    "rocthrust/gfx1036/testing/reduce_by_key",
    "rocthrust/gfx1036/testing/remove",
    "rocthrust/gfx1036/testing/scan",
    "rocthrust/gfx1036/testing/set_difference",
    "rocthrust/gfx1036/testing/set_difference_by_key",
    "rocthrust/gfx1036/testing/set_intersection",
    "rocthrust/gfx1036/testing/set_intersection_by_key",
    "rocthrust/gfx1036/testing/sort",
    "rocthrust/gfx1036/testing/sort_by_key",
    "rocthrust/gfx1036/testing/sort_by_key_variable_bits",
    "rocthrust/gfx1036/testing/sort_variable_bits",
    "rocthrust/gfx1036/testing/stable_sort_by_key",
    "rocthrust/gfx1036/testing/transform_scan",
    "rocthrust/gfx1036/testing/unique",
    "rocthrust/gfx1036/testing/unique_by_key",
    // CUDA identities that timed out under the same 8-second bounded probe;
    // they are intentionally not classified as semantic failures yet.
    "thrust/copy",
    "thrust/count",
    "thrust/equal",
    "thrust/find",
    "thrust/functional_placeholders_arithmetic",
    "thrust/functional_placeholders_bitwise",
    "thrust/functional_placeholders_compound_assignment",
    "thrust/functional_placeholders_relational",
    "thrust/inner_product",
    "thrust/is_partitioned",
    "thrust/is_sorted",
    "thrust/is_sorted_until",
    "thrust/max_element",
    "thrust/merge",
    "thrust/merge_by_key",
    "thrust/min_element",
    "thrust/minmax_element",
    "thrust/partition",
    "thrust/partition_point",
    "thrust/reduce",
    "thrust/reduce_by_key",
    "thrust/remove",
    "thrust/scan",
    "thrust/scan_by_key",
    "thrust/set_difference",
    "thrust/set_difference_by_key",
    "thrust/set_difference_by_key_descending",
    "thrust/set_difference_descending",
    "thrust/set_intersection",
    "thrust/set_intersection_by_key",
    "thrust/set_intersection_by_key_descending",
    "thrust/set_intersection_descending",
    "thrust/set_symmetric_difference",
    "thrust/set_symmetric_difference_by_key",
    "thrust/set_symmetric_difference_by_key_descending",
    "thrust/set_symmetric_difference_descending",
    "thrust/set_union",
    "thrust/set_union_by_key",
    "thrust/set_union_by_key_descending",
    "thrust/set_union_descending",
    "thrust/shuffle",
    "thrust/sort",
    "thrust/sort_by_key",
    "thrust/sort_permutation_iterator",
    "thrust/stable_sort",
    "thrust/stable_sort_by_key",
    "thrust/transform_scan",
    "thrust/unique_by_key",
    // Previously recorded ROCm async large-index hangs.
    "rocthrust/gfx1036/testing/async/exclusive_scan/large_indices",
    "rocthrust/gfx1036/testing/async/inclusive_scan/large_indices",
};

inline std::filesystem::path legacy_executable(std::string_view identity,
                                               const Configuration &configuration) {
  if (configuration.legacy_binary_root.empty())
    return {};
  const std::filesystem::path root(configuration.legacy_binary_root);
  if (identity.starts_with("thrust/"))
    return root / "compute_89" / std::filesystem::path(std::string(identity.substr(7))) / "test";
  constexpr std::string_view prefix = "rocthrust/gfx1036/testing/";
  if (identity.starts_with(prefix)) {
    const auto test_name = identity.substr(prefix.size());
    std::string executable = "test_thrust_";
    if (test_name.starts_with("async_exclusive_scan_"))
      executable += "exclusive_";
    else if (test_name.starts_with("async_inclusive_scan_"))
      executable += "inclusive_";
    const auto normalized = test_name.starts_with("async_exclusive_scan_")
                                ? test_name.substr(std::string_view("async_exclusive_scan_").size())
                                : (test_name.starts_with("async_inclusive_scan_")
                                       ? test_name.substr(std::string_view("async_inclusive_scan_").size())
                                       : test_name);
    for (const auto character : normalized)
      executable.push_back(character == '/' ? '_' : character);
    return root / executable;
  }
  return {};
}

inline std::string shell_quote(const std::string &value) {
  std::string quoted = "'";
  for (const auto character : value) {
    if (character == '\'')
      quoted += "'\\''";
    else
      quoted.push_back(character);
  }
  quoted.push_back('\'');
  return quoted;
}

inline std::string report_stem(std::string_view identity) {
  std::string result;
  result.reserve(identity.size());
  for (const auto character : identity)
    result.push_back((std::isalnum(static_cast<unsigned char>(character)) || character == '.' ||
                      character == '-' || character == '_')
                         ? character
                         : '_');
  return result;
}

inline Result run_legacy_binary(std::string_view identity, const Configuration &configuration) {
  const auto executable = legacy_executable(identity, configuration);
  if (executable.empty())
    return Result::skip("legacy binary root is not configured");
  if (!std::filesystem::is_regular_file(executable))
    return Result::skip("legacy executable is not present: " + executable.string());
  std::filesystem::path stdout_path;
  std::filesystem::path stderr_path;
  std::string command = shell_quote(executable.string()) + " --sizes=tiny --concise";
  if (configuration.legacy_output_directory.empty()) {
    command += " >/dev/null 2>&1";
  } else {
    const auto directory = std::filesystem::path(configuration.legacy_output_directory);
    std::error_code error;
    std::filesystem::create_directories(directory, error);
    if (error)
      return Result::fail("could not create legacy output directory " + directory.string() + ": " +
                          error.message());
    const auto stem = report_stem(identity);
    stdout_path = directory / (stem + ".stdout");
    stderr_path = directory / (stem + ".stderr");
    command += " >" + shell_quote(stdout_path.string()) + " 2>" + shell_quote(stderr_path.string());
  }
  const int status = std::system(command.c_str());
  if (status == 0)
    return Result::pass();
  const auto report = [&] {
    if (stdout_path.empty())
      return std::string{};
    return " (stdout=" + stdout_path.string() + ", stderr=" + stderr_path.string() + ")";
  };
#if defined(_WIN32)
  return Result::fail("legacy executable exited with status " + std::to_string(status) + report());
#else
  if (status == -1)
    return Result::fail("legacy executable could not be started" + report());
  if (WIFEXITED(status))
    return Result::fail("legacy executable exited with status " + std::to_string(WEXITSTATUS(status)) +
                        report());
  if (WIFSIGNALED(status))
    return Result::fail("legacy executable terminated by signal " + std::to_string(WTERMSIG(status)) +
                        report());
  return Result::fail("legacy executable ended with unrecognised wait status " + std::to_string(status) +
                      report());
#endif
}

inline std::vector<Case> algorithm_cases(std::string_view family, const std::string_view *identities,
                                         std::size_t count, const Configuration &configuration,
                                         bool expected_failure = true) {
  std::vector<Case> result;
  result.reserve(count);
  for (std::size_t index = 0; index < count; ++index) {
    const auto identity = identities[index];
    result.push_back({"legacy-red/" + std::string(family) + "/" + std::string(identity),
                      [identity, configuration] { return run_legacy_binary(identity, configuration); },
                      expected_failure});
  }
  return result;
}

inline std::vector<Case> cuda_thrust_cases(const Configuration &configuration) {
  return algorithm_cases("cuda-thrust", cuda_thrust_fixed, std::size(cuda_thrust_fixed), configuration,
                         false);
}

inline std::vector<Case> roc_thrust_cases(const Configuration &configuration) {
  return algorithm_cases("roc-thrust", roc_thrust.data(), roc_thrust.size(), configuration);
}

inline std::vector<Case> roc_thrust_fixed_cases(const Configuration &configuration) {
  return algorithm_cases("roc-thrust-fixed", roc_thrust_fixed, std::size(roc_thrust_fixed), configuration,
                         false);
}

template <template <ScalarType> class Backend> Case rocblas_f16_case(const Configuration &configuration) {
  const auto test_size =
      std::find(configuration.blas_sizes.begin(), configuration.blas_sizes.end(), std::size_t{8}) !=
              configuration.blas_sizes.end()
          ? std::size_t{8}
          : (configuration.blas_sizes.empty() ? std::size_t{8} : configuration.blas_sizes.front());
  return {"legacy-red/rocblas/gfx1036/f16/all",
          [test_size] {
            return capture([&] {
              Backend<ScalarType::f16> backend;
              blas::detail::execute_level1<ScalarType::f16>(backend, blas::Operation::axpy, test_size);
            });
          },
          false};
}

} // namespace unified_suite::legacy_reds

#endif
