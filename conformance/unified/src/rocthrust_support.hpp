#pragma once

// Focused rocThrust semantic probes derived from the historical gfx1036 red
// cases. These are kept separate from the portable algorithm cases:
// the operations below are rocThrust overload/iterator forms, rather than
// another spelling of the already-covered C++ algorithm semantics.

#if defined(__CUDACC__) || defined(__HIPCC__)
  #define UNIFIED_ROCTHRUST_HOST_DEVICE __host__ __device__
#else
  #define UNIFIED_ROCTHRUST_HOST_DEVICE
#endif

namespace unified::rocthrust_support {
template <class T> using Pair = thrust::pair<T, int32_t>;

template <class T> struct PairLess {
  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(const Pair<T> &left, const Pair<T> &right) const {
    return left.first < right.first || (!(right.first < left.first) && left.second < right.second);
  }
};

// rocThrust's key/value set-operation overloads compare keys only.  The value
// iterator supplies payloads associated with the selected key; it is not part
// of set membership or equivalence.
template <class T> struct PairKeyLess {
  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(const Pair<T> &left, const Pair<T> &right) const { return left.first < right.first; }
};

template <class T> struct TupleLess {
  template <class LeftTuple, class RightTuple>
  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(const LeftTuple &left, const RightTuple &right) const {
    const auto left_key = thrust::get<0>(left);
    const auto right_key = thrust::get<0>(right);
    const auto left_value = thrust::get<1>(left);
    const auto right_value = thrust::get<1>(right);
    return left_key < right_key || (!(right_key < left_key) && left_value < right_value);
  }
};

template <class T> struct TupleCode {
  template <class Tuple> UNIFIED_ROCTHRUST_HOST_DEVICE int64_t operator()(const Tuple &value) const {
    return static_cast<int64_t>(thrust::get<0>(value)) * 1000 + static_cast<int64_t>(thrust::get<1>(value));
  }
};

template <class T> struct PairPlus {
  UNIFIED_ROCTHRUST_HOST_DEVICE Pair<T> operator()(const Pair<T> &left, const Pair<T> &right) const {
    return {left.first + right.first, left.second + right.second};
  }
};

template <class T> struct TuplePlus {
  using Tuple = thrust::tuple<T, int32_t>;

  UNIFIED_ROCTHRUST_HOST_DEVICE Tuple operator()(Tuple left, Tuple right) const {
    return thrust::make_tuple(thrust::get<0>(left) + thrust::get<0>(right), thrust::get<1>(left) + thrust::get<1>(right));
  }
};

template <class T> struct Square {
  UNIFIED_ROCTHRUST_HOST_DEVICE T operator()(T value) const { return value * value; }
};

template <class T> struct Negate {
  UNIFIED_ROCTHRUST_HOST_DEVICE T operator()(T value) const { return -value; }
};

enum class SetKind { union_set, intersection, difference, symmetric_difference };

template <class T> struct KeyValues {
  std::vector<T> keys;
  std::vector<int32_t> values;
  friend bool operator==(const KeyValues &a, const KeyValues &b) { return a.keys == b.keys && a.values == b.values; }
};

template <class T> inline std::vector<T> ascending_keys() {
  return {T{-5}, T{-5}, T{-3}, T{-1}, T{-1}, T{0}, T{2}, T{2}, T{2}, T{4}, T{7}};
}

template <class T> inline std::vector<T> descending_keys() {
  auto result = ascending_keys<T>();
  std::reverse(result.begin(), result.end());
  return result;
}

inline std::vector<int32_t> payload(size_t count, int32_t base) {
  std::vector<int32_t> result(count);
  for (size_t index = 0; index < count; ++index)
    result[index] = base + static_cast<int32_t>(index);
  return result;
}

template <class T> inline KeyValues<T> descending_key_values(const std::vector<T> &keys, int32_t base) {
  return {keys, payload(keys.size(), base)};
}

template <class T>
inline KeyValues<T> reference_set_by_key(const KeyValues<T> &left, const KeyValues<T> &right, SetKind kind, bool descending) {
  KeyValues<T> result;
  const auto less = [descending](const T &a, const T &b) { return descending ? b < a : a < b; };
  size_t a = 0;
  size_t b = 0;
  while (a < left.keys.size() || b < right.keys.size()) {
    if (a == left.keys.size()) {
      if (kind == SetKind::union_set || kind == SetKind::symmetric_difference) {
        result.keys.push_back(right.keys[b]);
        result.values.push_back(right.values[b]);
      }
      ++b;
    } else if (b == right.keys.size()) {
      if (kind != SetKind::intersection) {
        result.keys.push_back(left.keys[a]);
        result.values.push_back(left.values[a]);
      }
      ++a;
    } else if (less(left.keys[a], right.keys[b])) {
      if (kind != SetKind::intersection) {
        result.keys.push_back(left.keys[a]);
        result.values.push_back(left.values[a]);
      }
      ++a;
    } else if (less(right.keys[b], left.keys[a])) {
      if (kind == SetKind::union_set || kind == SetKind::symmetric_difference) {
        result.keys.push_back(right.keys[b]);
        result.values.push_back(right.values[b]);
      }
      ++b;
    } else {
      if (kind == SetKind::union_set || kind == SetKind::intersection) {
        result.keys.push_back(left.keys[a]);
        result.values.push_back(left.values[a]);
      }
      ++a;
      ++b;
    }
  }
  return result;
}

template <class T>
inline KeyValues<T> thrust_set_by_key(const KeyValues<T> &left, const KeyValues<T> &right, SetKind kind, bool descending) {
  const thrust::device_vector<T> left_keys(left.keys.begin(), left.keys.end());
  const thrust::device_vector<T> right_keys(right.keys.begin(), right.keys.end());
  const thrust::device_vector<int32_t> left_values(left.values.begin(), left.values.end());
  const thrust::device_vector<int32_t> right_values(right.values.begin(), right.values.end());
  thrust::device_vector<T> output_keys(left.keys.size() + right.keys.size());
  thrust::device_vector<int32_t> output_values(left.values.size() + right.values.size());

  thrust::pair<typename thrust::device_vector<T>::iterator, typename thrust::device_vector<int32_t>::iterator> ends;
  if (kind == SetKind::union_set) {
    ends = descending
               ? thrust::set_union_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(), left_values.begin(),
                                          right_values.begin(), output_keys.begin(), output_values.begin(), algorithms::Greater<T>{})
               : thrust::set_union_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(), left_values.begin(),
                                          right_values.begin(), output_keys.begin(), output_values.begin());
  } else if (kind == SetKind::intersection) {
    ends = descending
               ? thrust::set_intersection_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(),
                                                 left_values.begin(), output_keys.begin(), output_values.begin(), algorithms::Greater<T>{})
               : thrust::set_intersection_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(),
                                                 left_values.begin(), output_keys.begin(), output_values.begin());
  } else if (kind == SetKind::difference) {
    ends =
        descending
            ? thrust::set_difference_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(), left_values.begin(),
                                            right_values.begin(), output_keys.begin(), output_values.begin(), algorithms::Greater<T>{})
            : thrust::set_difference_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(), left_values.begin(),
                                            right_values.begin(), output_keys.begin(), output_values.begin());
  } else {
    ends = descending ? thrust::set_symmetric_difference_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(),
                                                                left_values.begin(), right_values.begin(), output_keys.begin(),
                                                                output_values.begin(), algorithms::Greater<T>{})
                      : thrust::set_symmetric_difference_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(),
                                                                left_values.begin(), right_values.begin(), output_keys.begin(),
                                                                output_values.begin());
  }
  output_keys.resize(static_cast<size_t>(ends.first - output_keys.begin()));
  output_values.resize(static_cast<size_t>(ends.second - output_values.begin()));
  return {std::vector<T>(output_keys.begin(), output_keys.end()), std::vector<int32_t>(output_values.begin(), output_values.end())};
}

template <class T> inline std::vector<Pair<T>> pair_values(const std::vector<T> &keys, int32_t base) {
  std::vector<Pair<T>> result;
  const auto values = payload(keys.size(), base);
  result.reserve(keys.size());
  for (size_t index = 0; index < keys.size(); ++index)
    result.emplace_back(keys[index], values[index]);
  return result;
}

template <class T> inline std::vector<Pair<T>> host_pairs(const thrust::device_vector<Pair<T>> &values, size_t count) {
  return {values.begin(), values.begin() + static_cast<ptrdiff_t>(count)};
}

template <class T>
inline std::vector<Pair<T>> thrust_set_pairs(const std::vector<Pair<T>> &left, const std::vector<Pair<T>> &right, SetKind kind) {
  const thrust::device_vector<Pair<T>> a(left.begin(), left.end());
  const thrust::device_vector<Pair<T>> b(right.begin(), right.end());
  thrust::device_vector<Pair<T>> output(left.size() + right.size());
  auto end = output.begin();
  if (kind == SetKind::union_set) end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::intersection)
    end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::difference)
    end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  else end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  return host_pairs(output, static_cast<size_t>(end - output.begin()));
}

template <class T> inline std::vector<Pair<T>> pair_expected(std::vector<Pair<T>> left, std::vector<Pair<T>> right, SetKind kind) {
  std::stable_sort(left.begin(), left.end(), PairKeyLess<T>{});
  std::stable_sort(right.begin(), right.end(), PairKeyLess<T>{});
  std::vector<Pair<T>> result(left.size() + right.size());
  auto end = result.begin();
  if (kind == SetKind::union_set)
    end = std::set_union(left.begin(), left.end(), right.begin(), right.end(), result.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::intersection)
    end = std::set_intersection(left.begin(), left.end(), right.begin(), right.end(), result.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::difference)
    end = std::set_difference(left.begin(), left.end(), right.begin(), right.end(), result.begin(), PairKeyLess<T>{});
  else end = std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), result.begin(), PairKeyLess<T>{});
  result.erase(end, result.end());
  return result;
}
} // namespace unified::rocthrust_support
