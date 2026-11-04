patch_exact("${source_root}.staging/main.cpp"
  [==[exit((k <= max_iter) ? 0 : 1);]==]
  [==[exit((k <= max_iter && err < tol) ? 0 : 1);]==])
