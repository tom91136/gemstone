patch_exact("${source_root}.staging/simpleCudaGraphs.cu"
  [==[typedef struct callBackData]==]
  [==[static double expected_sum = 0.0;
static bool sum_mismatch = false;
typedef struct callBackData]==])
patch_exact("${source_root}.staging/simpleCudaGraphs.cu"
  [==[init_input(inputVec_h, size);]==]
  [==[init_input(inputVec_h, size);
  for (size_t i = 0; i < size; i++) expected_sum += inputVec_h[i];]==])
patch_exact("${source_root}.staging/simpleCudaGraphs.cu"
  [==[*result = 0.0;]==]
  [==[if (*result < expected_sum * 0.999 || *result > expected_sum * 1.001) sum_mismatch = true;
  *result = 0.0;]==])
patch_exact("${source_root}.staging/simpleCudaGraphs.cu"
  [==[return EXIT_SUCCESS;]==]
  [==[return sum_mismatch ? EXIT_FAILURE : EXIT_SUCCESS;]==])
