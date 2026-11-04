patch_exact("${source_root}.staging/binaryPartitionCG.cu"
  [==[printf("Array size]==]
  [==[int odd_count = 0, odd_sum = 0, even_sum = 0;
  for (int i = 0; i < arrSize; i++) {
    if (h_inputArr[i] & 1) {
      odd_count++;
      odd_sum += h_inputArr[i];
    }
    else {
      even_sum += h_inputArr[i];
    }
  }
  if (odd_count != h_numOfOdds[0] || odd_sum != h_sumOfOddEvenElems[0] || even_sum != h_sumOfOddEvenElems[1])
    return EXIT_FAILURE;
  printf("Array size]==])
