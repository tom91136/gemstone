#ifdef POLY_NATIVE_TRIG
#define POLY_SIN native_sin
#define POLY_COS native_cos
#define POLY_TAN native_tan
#else
#define POLY_SIN sin
#define POLY_COS cos
#define POLY_TAN tan
#endif
#define POLYREGION_OPENCL_NULL_POINTER_OFFSET ((ulong)-1)
typedef struct test_utils_h_54_21_6e547ca9f9e8703d_e59bcf61b6c68a54 test_utils_h_54_21_6e547ca9f9e8703d_e59bcf61b6c68a54;
typedef struct check_control_break_in_cond_cpp_21_42_f5dc9e8ba3be5e85 check_control_break_in_cond_cpp_21_42_f5dc9e8ba3be5e85;
struct check_control_break_in_cond_cpp_21_42_f5dc9e8ba3be5e85 {
  int n;
};

struct test_utils_h_54_21_6e547ca9f9e8703d_e59bcf61b6c68a54 {
  global int* result;
  check_control_break_in_cond_cpp_21_42_f5dc9e8ba3be5e85 f;
};

kernel void _main(global char* _polyregion_arg_base_0, ulong _polyregion_arg_byte_offset_0);


kernel void _main(global char* _polyregion_arg_base_0, ulong _polyregion_arg_byte_offset_0) {
  global char* _v0 = _polyregion_arg_byte_offset_0 == POLYREGION_OPENCL_NULL_POINTER_OFFSET ? ((global char*) 0) : ((global char*) (((global uchar*) _polyregion_arg_base_0) + _polyregion_arg_byte_offset_0));
  global test_utils_h_54_21_6e547ca9f9e8703d_e59bcf61b6c68a54* _v1 = ((global test_utils_h_54_21_6e547ca9f9e8703d_e59bcf61b6c68a54*) _v0);
  uint _v2 = get_global_id(0);
  char _v3 = (_v2 == 0);
  if (_v3) {
    int _v4 = 0;
    int _v5 = 0;
    char _v6 = (_v5 < (*_v1).f.n);
    char _v7 = _v6;
    while(_v7) {
      char _v8 = (_v5 == 5);
      if (_v8) {
        break;
      }
      int _v9 = (_v4 + _v5);
      _v4 = _v9;
      int _va = _v5;
      int _vb = (_va + 1);
      _v5 = _vb;
      char _vc = (_v5 < (*_v1).f.n);
      _v7 = _vc;
    }
    int _vd = _v4;
    ulong _ve = ((ulong) (*_v1).result);
    global char* _vf = &(_v0 /*char*/)[_ve];
    global int* _vg = ((global int*) _vf);
    _vg[0] = _vd;
  }
  return /*void*/;
}
