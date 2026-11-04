patch_exact("${source_root}.staging/radixSortThrust.cu"
  [==[thrust::is_sorted(h_keysSorted.begin(), h_keysSorted.end());]==]
  [==[thrust::is_sorted(h_keysSorted.begin(), h_keysSorted.end());
  thrust::host_vector<T> h_expected(h_keys);
  thrust::sort(h_expected.begin(), h_expected.end());
  bTestResult = bTestResult && h_expected == h_keysSorted;]==])
