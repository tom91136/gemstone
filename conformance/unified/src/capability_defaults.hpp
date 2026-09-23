#pragma once

#include <cstddef>

namespace unified::backend {

// Shared capability defaults. Adapters implement one capability predicate for
// their device/type and inherit the per-operation predicates, overriding only
// the operations with a narrower condition.
template <class Derived> struct AlgorithmCapabilities {
  template <class T> bool supports_adjacent_difference(size_t) const {
    return static_cast<const Derived *>(this)->template capability<T>();
  }
  template <class T> bool supports_all_of(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_any_of(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_binary_search(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_copy(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_copy_if(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_copy_n(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_count(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_count_if(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_equal(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_equal_range(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_exclusive_scan(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_exclusive_scan_by_key(size_t) const {
    return static_cast<const Derived *>(this)->template capability<T>();
  }
  template <class T> bool supports_fill(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_find(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_find_if(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_for_each(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_gather(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_generate(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_includes(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_includes_false(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_inclusive_scan(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_inclusive_scan_by_key(size_t) const {
    return static_cast<const Derived *>(this)->template capability<T>();
  }
  template <class T> bool supports_inner_product(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_inplace_merge(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_is_partitioned(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_is_partitioned_false(size_t) const {
    return static_cast<const Derived *>(this)->template capability<T>();
  }
  template <class T> bool supports_is_sorted(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_is_sorted_false(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_is_sorted_until(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_lower_bound(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_max_element(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_merge(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_merge_by_key(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_min_element(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_minmax_element(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_mismatch(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_none_of(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_nth_element(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_partial_sort(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_partition(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_partition_copy(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_partition_point(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_reduce(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_reduce_by_key(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_remove_copy_if(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_remove_if(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_replace_copy_if(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_replace_if(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_reverse(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_reverse_copy(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_rotate(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_rotate_copy(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_scatter(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_set_difference(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_set_intersection(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_set_symmetric_difference(size_t) const {
    return static_cast<const Derived *>(this)->template capability<T>();
  }
  template <class T> bool supports_set_union(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_shuffle(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_sort(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_sort_by_key(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_sort_descending(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_stable_partition(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_stable_sort(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_stable_sort_by_key(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_swap_ranges(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_transform_binary(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_transform_exclusive_scan(size_t) const {
    return static_cast<const Derived *>(this)->template capability<T>();
  }
  template <class T> bool supports_transform_inclusive_scan(size_t) const {
    return static_cast<const Derived *>(this)->template capability<T>();
  }
  template <class T> bool supports_transform_reduce(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_transform_unary(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_uninitialized_copy(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_uninitialized_fill(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_unique(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_unique_by_key(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_unique_copy(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_upper_bound(size_t) const { return static_cast<const Derived *>(this)->template capability<T>(); }
};

template <class Derived> struct EdgeCapabilities {
  template <class T> bool supports_biased_index_64() const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_composed_iterator_reduce() const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_dependency_diamond() const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_inplace_scan_canary() const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_numeric_edge_roundtrip() const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_primitive_block_scan() const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_runtime_error_recover() const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_segments_boundary() const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_segments_giant() const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_segments_singleton() const { return static_cast<const Derived *>(this)->template capability<T>(); }
  template <class T> bool supports_stable_sort_payload() const { return static_cast<const Derived *>(this)->template capability<T>(); }
};

} // namespace unified::backend
