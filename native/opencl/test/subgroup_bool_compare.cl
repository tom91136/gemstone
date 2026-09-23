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
typedef struct check_gpu_subgroup_intrinsics_cpp_29_27_92e8b5a2241cd0a3 check_gpu_subgroup_intrinsics_cpp_29_27_92e8b5a2241cd0a3;
typedef struct test_utils_h_97_17_25112ec259c36797_56e8e43864f8bd14 test_utils_h_97_17_25112ec259c36797_56e8e43864f8bd14;
typedef struct check_gpu_subgroup_intrinsics_cpp_14_40_570ec2893f1682d0 check_gpu_subgroup_intrinsics_cpp_14_40_570ec2893f1682d0;
struct check_gpu_subgroup_intrinsics_cpp_14_40_570ec2893f1682d0 {
  global uint (*result)[32];
  global uint* runtimeMask;
};

struct check_gpu_subgroup_intrinsics_cpp_29_27_92e8b5a2241cd0a3 {
  uint active;
};

struct test_utils_h_97_17_25112ec259c36797_56e8e43864f8bd14 {
  check_gpu_subgroup_intrinsics_cpp_14_40_570ec2893f1682d0 f;
};

kernel void _main(global char* _polyregion_arg_base_0, ulong _polyregion_arg_byte_offset_0);


kernel void _main(global char* _polyregion_arg_base_0, ulong _polyregion_arg_byte_offset_0) {
  global char* _v0 = _polyregion_arg_byte_offset_0 == POLYREGION_OPENCL_NULL_POINTER_OFFSET ? ((global char*) 0) : ((global char*) (((global uchar*) _polyregion_arg_base_0) + _polyregion_arg_byte_offset_0));
  local uint _v2[1024];
  global test_utils_h_97_17_25112ec259c36797_56e8e43864f8bd14* _v1 = ((global test_utils_h_97_17_25112ec259c36797_56e8e43864f8bd14*) _v0);

  uint _v3 = get_global_id(0);
  uint _v4 = get_local_id(0);
  uint _v5 = get_local_id(1);
  uint _v6 = get_local_id(2);
  uint _v7 = get_local_size(0);
  uint _v8 = get_local_size(1);
  uint _v9 = get_local_size(2);
  uint _va = (_v8 * _v6);
  uint _vb = (_v5 + _va);
  uint _vc = (_v7 * _vb);
  uint _vd = (_v4 + _vc);
  uint _ve = (_v7 * _v8);
  uint _vf = (_ve * _v9);
  uint _vg = (_vd & 31);
  uint _vh = 32;
  ulong _vi = ((ulong) (*_v1).f.runtimeMask);
  global char* _vj = &(_v0 /*char*/)[_vi];
  global uint* _vk = ((global uint*) _vj);
  uint _vl = _vk[0];
  uint _vm = get_local_id(0);
  uint _vn = get_local_id(1);
  uint _vo = get_local_id(2);
  uint _vp = get_local_size(0);
  uint _vq = get_local_size(1);
  uint _vr = get_local_size(2);
  uint _vs = (_vq * _vo);
  uint _vt = (_vn + _vs);
  uint _vu = (_vp * _vt);
  uint _vv = (_vm + _vu);
  uint _vw = (_vp * _vq);
  uint _vx = (_vw * _vr);
  uint _vy = (_vv & 31);
  uint _vz = (_vv - _vy);
  uint _v10 = (_vy & 31);
  uint _v11 = (_vl >> _v10);
  uint _v12 = (_v11 & 1);
  char _v13 = (_vy < 32);
  char _v14 = (_v12 != 0);
  char _v15 = (_v13 && _v14);
  char _v16 = (true && _v15);
  uint _v17 = 0;
  if (_v16) {
    _v17 = 1;
  }
  _v2[_vv] = _v17;
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v19 = 0;
  uint _v1a = 0;
  uint _v1b = 0;
  for(uint _v1c = 0; _v1c < 32; _v1c += 1) {
    _v1a = (_vz + _v1c);
    char _v1d = (_v1a < _vx);
    _v1b = 0;
    if (_v1d) {
      _v1b = _v2[_v1a];
    }
    char _v1e = (_v1b != 0);
    uint _v1f = (1 << _v1c);
    if (_v1e) {
      _v19 = (_v19 | _v1f);
    }
  }
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v1h = _v19;
  ulong _v1i = ((ulong) (*_v1).f.runtimeMask);
  global char* _v1j = &(_v0 /*char*/)[_v1i];
  global uint* _v1k = ((global uint*) _v1j);
  uint _v1l = _v1k[0];
  uint _v1m = get_local_id(0);
  uint _v1n = get_local_id(1);
  uint _v1o = get_local_id(2);
  uint _v1p = get_local_size(0);
  uint _v1q = get_local_size(1);
  uint _v1r = get_local_size(2);
  uint _v1s = (_v1q * _v1o);
  uint _v1t = (_v1n + _v1s);
  uint _v1u = (_v1p * _v1t);
  uint _v1v = (_v1m + _v1u);
  uint _v1w = (_v1p * _v1q);
  uint _v1x = (_v1w * _v1r);
  uint _v1y = (_v1v & 31);
  uint _v1z = (_v1v - _v1y);
  _v2[_v1v] = _vg;
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v21 = (~31);
  uint _v22 = (_v1y & _v21);
  uint _v23 = (_v22 | 31);
  uint _v24 = (_v1y + 1);
  uint _v25 = (_v1y & 31);
  uint _v26 = (_v1l >> _v25);
  uint _v27 = (_v26 & 1);
  char _v28 = (_v1y < 32);
  char _v29 = (_v27 != 0);
  char _v2a = (_v28 && _v29);
  uint _v2b = (_v24 & 31);
  uint _v2c = (_v1l >> _v2b);
  uint _v2d = (_v2c & 1);
  char _v2e = (_v24 < 32);
  char _v2f = (_v2d != 0);
  char _v2g = (_v2e && _v2f);
  uint _v2h = (_v1z + _v24);
  char _v2i = (_v24 >= _v22);
  char _v2j = (_v24 <= _v23);
  char _v2k = (_v2i && _v2j);
  char _v2l = (_v24 < 32);
  char _v2m = (_v2h < _v1x);
  char _v2n = (_v2k && _v2l);
  char _v2o = (_v2n && _v2m);
  char _v2p = (_v2a && _v2g);
  char _v2q = (_v2o && _v2p);
  uint _v2r;
  if (_v2q) {
    _v2r = _v2[_v2h];
  } else {
    _v2r = _vg;
  }
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v2t = _v2r;
  ulong _v2u = ((ulong) (*_v1).f.runtimeMask);
  global char* _v2v = &(_v0 /*char*/)[_v2u];
  global uint* _v2w = ((global uint*) _v2v);
  uint _v2x = _v2w[0];
  uint _v2y = get_local_id(0);
  uint _v2z = get_local_id(1);
  uint _v30 = get_local_id(2);
  uint _v31 = get_local_size(0);
  uint _v32 = get_local_size(1);
  uint _v33 = get_local_size(2);
  uint _v34 = (_v32 * _v30);
  uint _v35 = (_v2z + _v34);
  uint _v36 = (_v31 * _v35);
  uint _v37 = (_v2y + _v36);
  uint _v38 = (_v31 * _v32);
  uint _v39 = (_v38 * _v33);
  uint _v3a = (_v37 & 31);
  uint _v3b = (_v37 - _v3a);
  _v2[_v37] = _vg;
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v3d = (~31);
  uint _v3e = (_v3a & _v3d);
  uint _v3f = (_v3e | 31);
  uint _v3g = (_v3a - 1);
  uint _v3h = (_v3a & 31);
  uint _v3i = (_v2x >> _v3h);
  uint _v3j = (_v3i & 1);
  char _v3k = (_v3a < 32);
  char _v3l = (_v3j != 0);
  char _v3m = (_v3k && _v3l);
  uint _v3n = (_v3g & 31);
  uint _v3o = (_v2x >> _v3n);
  uint _v3p = (_v3o & 1);
  char _v3q = (_v3g < 32);
  char _v3r = (_v3p != 0);
  char _v3s = (_v3q && _v3r);
  uint _v3t = (_v3b + _v3g);
  char _v3u = (_v3g >= _v3e);
  char _v3v = (_v3g <= _v3f);
  char _v3w = (_v3u && _v3v);
  char _v3x = (_v3g < 32);
  char _v3y = (_v3t < _v39);
  char _v3z = (_v3w && _v3x);
  char _v40 = (_v3z && _v3y);
  char _v41 = (_v3m && _v3s);
  char _v42 = (_v40 && _v41);
  uint _v43;
  if (_v42) {
    _v43 = _v2[_v3t];
  } else {
    _v43 = _vg;
  }
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v45 = _v43;
  uint _v46 = get_local_id(0);
  uint _v47 = get_local_id(1);
  uint _v48 = get_local_id(2);
  uint _v49 = get_local_size(0);
  uint _v4a = get_local_size(1);
  uint _v4b = get_local_size(2);
  uint _v4c = (_v4a * _v48);
  uint _v4d = (_v47 + _v4c);
  uint _v4e = (_v49 * _v4d);
  uint _v4f = (_v46 + _v4e);
  uint _v4g = (_v49 * _v4a);
  uint _v4h = (_v4g * _v4b);
  uint _v4i = (_v4f & 31);
  uint _v4j = (_v4f - _v4i);
  _v2[_v4f] = _vg;
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v4l = (~31);
  uint _v4m = (_v4i & _v4l);
  uint _v4n = (_v4m | 31);
  uint _v4o = (0 & 31);
  uint _v4p = (_v4m | _v4o);
  uint _v4q = (_v4i & 31);
  uint _v4r = (-1 >> _v4q);
  uint _v4s = (_v4r & 1);
  char _v4t = (_v4i < 32);
  char _v4u = (_v4s != 0);
  char _v4v = (_v4t && _v4u);
  uint _v4w = (_v4p & 31);
  uint _v4x = (-1 >> _v4w);
  uint _v4y = (_v4x & 1);
  char _v4z = (_v4p < 32);
  char _v50 = (_v4y != 0);
  char _v51 = (_v4z && _v50);
  uint _v52 = (_v4j + _v4p);
  char _v53 = (_v4p >= _v4m);
  char _v54 = (_v4p <= _v4n);
  char _v55 = (_v53 && _v54);
  char _v56 = (_v4p < 32);
  char _v57 = (_v52 < _v4h);
  char _v58 = (_v55 && _v56);
  char _v59 = (_v58 && _v57);
  char _v5a = (_v4v && _v51);
  char _v5b = (_v59 && _v5a);
  uint _v5c;
  if (_v5b) {
    _v5c = _v2[_v52];
  } else {
    _v5c = _vg;
  }
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v5e = _v5c;
  ulong _v5f = ((ulong) (*_v1).f.runtimeMask);
  global char* _v5g = &(_v0 /*char*/)[_v5f];
  global uint* _v5h = ((global uint*) _v5g);
  uint _v5i = _v5h[0];
  uint _v5j = get_local_id(0);
  uint _v5k = get_local_id(1);
  uint _v5l = get_local_id(2);
  uint _v5m = get_local_size(0);
  uint _v5n = get_local_size(1);
  uint _v5o = get_local_size(2);
  uint _v5p = (_v5n * _v5l);
  uint _v5q = (_v5k + _v5p);
  uint _v5r = (_v5m * _v5q);
  uint _v5s = (_v5j + _v5r);
  uint _v5t = (_v5m * _v5n);
  uint _v5u = (_v5t * _v5o);
  uint _v5v = (_v5s & 31);
  uint _v5w = (_v5s - _v5v);
  _v2[_v5s] = _vg;
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v5y = (~31);
  uint _v5z = (_v5v & _v5y);
  uint _v60 = (_v5z | 31);
  uint _v61 = (_v5v ^ 1);
  uint _v62 = (_v5v & 31);
  uint _v63 = (_v5i >> _v62);
  uint _v64 = (_v63 & 1);
  char _v65 = (_v5v < 32);
  char _v66 = (_v64 != 0);
  char _v67 = (_v65 && _v66);
  uint _v68 = (_v61 & 31);
  uint _v69 = (_v5i >> _v68);
  uint _v6a = (_v69 & 1);
  char _v6b = (_v61 < 32);
  char _v6c = (_v6a != 0);
  char _v6d = (_v6b && _v6c);
  uint _v6e = (_v5w + _v61);
  char _v6f = (_v61 >= _v5z);
  char _v6g = (_v61 <= _v60);
  char _v6h = (_v6f && _v6g);
  char _v6i = (_v61 < 32);
  char _v6j = (_v6e < _v5u);
  char _v6k = (_v6h && _v6i);
  char _v6l = (_v6k && _v6j);
  char _v6m = (_v67 && _v6d);
  char _v6n = (_v6l && _v6m);
  uint _v6o;
  if (_v6n) {
    _v6o = _v2[_v6e];
  } else {
    _v6o = _vg;
  }
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v6q = _v6o;
  ulong _v6r = ((ulong) (*_v1).f.runtimeMask);
  global char* _v6s = &(_v0 /*char*/)[_v6r];
  global uint* _v6t = ((global uint*) _v6s);
  uint _v6u = _v6t[0];
  uint _v6v = get_local_id(0);
  uint _v6w = get_local_id(1);
  uint _v6x = get_local_id(2);
  uint _v6y = get_local_size(0);
  uint _v6z = get_local_size(1);
  uint _v70 = get_local_size(2);
  uint _v71 = (_v6z * _v6x);
  uint _v72 = (_v6w + _v71);
  uint _v73 = (_v6y * _v72);
  uint _v74 = (_v6v + _v73);
  uint _v75 = (_v6y * _v6z);
  uint _v76 = (_v75 * _v70);
  uint _v77 = (_v74 & 31);
  uint _v78 = (_v74 - _v77);
  _v2[_v74] = _vg;
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v7a = (~7);
  uint _v7b = (_v77 & _v7a);
  uint _v7c = (_v7b | 7);
  uint _v7d = (0 & 7);
  uint _v7e = (_v7b | _v7d);
  uint _v7f = (_v77 & 31);
  uint _v7g = (_v6u >> _v7f);
  uint _v7h = (_v7g & 1);
  char _v7i = (_v77 < 32);
  char _v7j = (_v7h != 0);
  char _v7k = (_v7i && _v7j);
  uint _v7l = (_v7e & 31);
  uint _v7m = (_v6u >> _v7l);
  uint _v7n = (_v7m & 1);
  char _v7o = (_v7e < 32);
  char _v7p = (_v7n != 0);
  char _v7q = (_v7o && _v7p);
  uint _v7r = (_v78 + _v7e);
  char _v7s = (_v7e >= _v7b);
  char _v7t = (_v7e <= _v7c);
  char _v7u = (_v7s && _v7t);
  char _v7v = (_v7e < 32);
  char _v7w = (_v7r < _v76);
  char _v7x = (_v7u && _v7v);
  char _v7y = (_v7x && _v7w);
  char _v7z = (_v7k && _v7q);
  char _v80 = (_v7y && _v7z);
  uint _v81;
  if (_v80) {
    _v81 = _v2[_v7r];
  } else {
    _v81 = _vg;
  }
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v83 = _v81;
  uint _v84 = get_local_id(0);
  uint _v85 = get_local_id(1);
  uint _v86 = get_local_id(2);
  uint _v87 = get_local_size(0);
  uint _v88 = get_local_size(1);
  uint _v89 = get_local_size(2);
  uint _v8a = (_v88 * _v86);
  uint _v8b = (_v85 + _v8a);
  uint _v8c = (_v87 * _v8b);
  uint _v8d = (_v84 + _v8c);
  uint _v8e = (_v87 * _v88);
  uint _v8f = (_v8e * _v89);
  uint _v8g = (_v8d & 31);
  uint _v8h = (_v8d - _v8g);
  _v2[_v8d] = _vg;
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v8j = (~31);
  uint _v8k = (_v8g & _v8j);
  uint _v8l = (_v8k | 31);
  uint _v8m = (0 & 31);
  uint _v8n = (_v8k | _v8m);
  uint _v8o = (_v8g & 31);
  uint _v8p = (65535 >> _v8o);
  uint _v8q = (_v8p & 1);
  char _v8r = (_v8g < 32);
  char _v8s = (_v8q != 0);
  char _v8t = (_v8r && _v8s);
  uint _v8u = (_v8n & 31);
  uint _v8v = (65535 >> _v8u);
  uint _v8w = (_v8v & 1);
  char _v8x = (_v8n < 32);
  char _v8y = (_v8w != 0);
  char _v8z = (_v8x && _v8y);
  uint _v90 = (_v8h + _v8n);
  char _v91 = (_v8n >= _v8k);
  char _v92 = (_v8n <= _v8l);
  char _v93 = (_v91 && _v92);
  char _v94 = (_v8n < 32);
  char _v95 = (_v90 < _v8f);
  char _v96 = (_v93 && _v94);
  char _v97 = (_v96 && _v95);
  char _v98 = (_v8t && _v8z);
  char _v99 = (_v97 && _v98);
  uint _v9a;
  if (_v99) {
    _v9a = _v2[_v90];
  } else {
    _v9a = _vg;
  }
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _v9c = _v9a;
  ulong _v9d = ((ulong) (*_v1).f.runtimeMask);
  global char* _v9e = &(_v0 /*char*/)[_v9d];
  global uint* _v9f = ((global uint*) _v9e);
  uint _v9g = _v9f[0];
  char _v9h = (_vg == 0);
  uint _v9i = get_local_id(0);
  uint _v9j = get_local_id(1);
  uint _v9k = get_local_id(2);
  uint _v9l = get_local_size(0);
  uint _v9m = get_local_size(1);
  uint _v9n = get_local_size(2);
  uint _v9o = (_v9m * _v9k);
  uint _v9p = (_v9j + _v9o);
  uint _v9q = (_v9l * _v9p);
  uint _v9r = (_v9i + _v9q);
  uint _v9s = (_v9l * _v9m);
  uint _v9t = (_v9s * _v9n);
  uint _v9u = (_v9r & 31);
  uint _v9v = (_v9r - _v9u);
  uint _v9w = (_v9u & 31);
  uint _v9x = (_v9g >> _v9w);
  uint _v9y = (_v9x & 1);
  char _v9z = (_v9u < 32);
  char _va0 = (_v9y != 0);
  char _va1 = (_v9z && _va0);
  char _va2 = (_v9h && _va1);
  uint _va3 = 0;
  if (_va2) {
    _va3 = 1;
  }
  _v2[_v9r] = _va3;
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _va5 = 0;
  uint _va6 = 0;
  uint _va7 = 0;
  for(uint _va8 = 0; _va8 < 32; _va8 += 1) {
    _va6 = (_v9v + _va8);
    char _va9 = (_va6 < _v9t);
    _va7 = 0;
    if (_va9) {
      _va7 = _v2[_va6];
    }
    char _vaa = (_va7 != 0);
    uint _vab = (1 << _va8);
    if (_vaa) {
      _va5 = (_va5 | _vab);
    }
  }
  barrier(CLK_LOCAL_MEM_FENCE);
  uint _vad = _va5;
  ulong _vae = ((ulong) (*_v1).f.runtimeMask);
  global char* _vaf = &(_v0 /*char*/)[_vae];
  global uint* _vag = ((global uint*) _vaf);
  uint _vah = _vag[0];
  uint _vai = get_local_id(0);
  uint _vaj = get_local_id(1);
  uint _vak = get_local_id(2);
  uint _val = get_local_size(0);
  uint _vam = get_local_size(1);
  uint _van = get_local_size(2);
  uint _vao = (_vam * _vak);
  uint _vap = (_vaj + _vao);
  uint _vaq = (_val * _vap);
  uint _var = (_vai + _vaq);
  uint _vas = (_val * _vam);
  uint _vat = (_vas * _van);
  uint _vau = (_var & 31);
  uint _vav = (_var - _vau);
  uint _vaw = (_vau & 31);
  uint _vax = (_vah >> _vaw);
  uint _vay = (_vax & 1);
  char _vaz = (_vau < 32);
  char _vb0 = (_vay != 0);
  char _vb1 = (_vaz && _vb0);
  char _vb2 = _v9h;
  char _vb3 = (_vb1 && _vb2);
  uint _vb4 = 0;
  if (_vb3) {
    _vb4 = 1;
  }
  _v2[_var] = _vb4;
  barrier(CLK_LOCAL_MEM_FENCE);
  char _vb6 = false;
  for(uint _vb7 = 0; _vb7 < 32; _vb7 += 1) {
    uint _vb8 = (_vav + _vb7);
    char _vb9 = (_vb8 < _vat);
    uint _vba = 0;
    if (_vb9) {
      _vba = _v2[_vb8];
    }
    char _vbb = (_vba != 0);
    if (_vbb) {
      _vb6 = true;
    }
  }
  barrier(CLK_LOCAL_MEM_FENCE);
  char _vbd = _vb6;
  ulong _vbe = ((ulong) (*_v1).f.runtimeMask);
  global char* _vbf = &(_v0 /*char*/)[_vbe];
  global uint* _vbg = ((global uint*) _vbf);
  uint _vbh = _vbg[0];
  char _vbi = (_vg < _vh);
  uint _vbj = get_local_id(0);
  uint _vbk = get_local_id(1);
  uint _vbl = get_local_id(2);
  uint _vbm = get_local_size(0);
  uint _vbn = get_local_size(1);
  uint _vbo = get_local_size(2);
  uint _vbp = (_vbn * _vbl);
  uint _vbq = (_vbk + _vbp);
  uint _vbr = (_vbm * _vbq);
  uint _vbs = (_vbj + _vbr);
  uint _vbt = (_vbm * _vbn);
  uint _vbu = (_vbt * _vbo);
  uint _vbv = (_vbs & 31);
  uint _vbw = (_vbs - _vbv);
  uint _vbx = (_vbv & 31);
  uint _vby = (_vbh >> _vbx);
  uint _vbz = (_vby & 1);
  char _vc0 = (_vbv < 32);
  char _vc1 = (_vbz != 0);
  char _vc2 = (_vc0 && _vc1);
  char _vc3 = (!_vbi);
  char _vc4 = (_vc2 && _vc3);
  uint _vc5 = 0;
  if (_vc4) {
    _vc5 = 1;
  }
  _v2[_vbs] = _vc5;
  barrier(CLK_LOCAL_MEM_FENCE);
  char _vc7 = true;
  for(uint _vc8 = 0; _vc8 < 32; _vc8 += 1) {
    uint _vc9 = (_vbw + _vc8);
    char _vca = (_vc9 < _vbu);
    uint _vcb = 0;
    if (_vca) {
      _vcb = _v2[_vc9];
    }
    char _vcc = (_vcb != 0);
    if (_vcc) {
      _vc7 = false;
    }
  }
  barrier(CLK_LOCAL_MEM_FENCE);
  char _vce = _vc7;
  barrier(CLK_LOCAL_MEM_FENCE);
  check_gpu_subgroup_intrinsics_cpp_29_27_92e8b5a2241cd0a3 _vcg = {0};
  _vcg.active = 0;
  _vcg.active = _v1h;
  check_gpu_subgroup_intrinsics_cpp_29_27_92e8b5a2241cd0a3 _vch; _vch.active = _vcg.active;
  uint _vci = (_vg + 1);
  uint _vcj = (_vg + -1);
  uint _vck = (_vg ^ 1);
  private uint* _vcl;
  char _vcm;
  char _vcn = (_vci < 32);
  if (_vcn) {
    uint _vco = (1 << _vci);
    uint _vcp = (_vch.active & _vco);
    _vcm = (_vcp != 0);
  } else {
    _vcm = false;
  }
  char _vcq = _vcm;
  if (_vcq) {
    _vcl = &(_vci /*uint*/);
  } else {
    _vcl = &(_vg /*uint*/);
  }
  uint _vcr = _vcl[0];
  private uint* _vcs;
  char _vct;
  char _vcu = (_vg > 0);
  if (_vcu) {
    char _vcv;
    char _vcw = (_vcj < 32);
    if (_vcw) {
      uint _vcx = (1 << _vcj);
      uint _vcy = (_vch.active & _vcx);
      _vcv = (_vcy != 0);
    } else {
      _vcv = false;
    }
    _vct = _vcv;
  } else {
    _vct = false;
  }
  if (_vct) {
    _vcs = &(_vcj /*uint*/);
  } else {
    _vcs = &(_vg /*uint*/);
  }
  uint _vcz = _vcs[0];
  private uint* _vd0;
  char _vd1;
  char _vd2 = (_vck < 32);
  if (_vd2) {
    uint _vd3 = (1 << _vck);
    uint _vd4 = (_vch.active & _vd3);
    _vd1 = (_vd4 != 0);
  } else {
    _vd1 = false;
  }
  char _vd5 = _vd1;
  if (_vd5) {
    _vd0 = &(_vck /*uint*/);
  } else {
    _vd0 = &(_vg /*uint*/);
  }
  uint _vd6 = _vd0[0];
  uint _vd7;
  char _vd8 = (_vh < 1);
  if (_vd8) {
    _vd7 = 1;
  } else {
    _vd7 = 0;
  }
  uint _vd9;
  char _vda = (_v2t != _vcr);
  if (_vda) {
    _vd9 = 2;
  } else {
    _vd9 = 0;
  }
  uint _vdb = (_vd7 | _vd9);
  uint _vdc;
  char _vdd = (_v45 != _vcz);
  if (_vdd) {
    _vdc = 4;
  } else {
    _vdc = 0;
  }
  uint _vde = (_vdb | _vdc);
  uint _vdf;
  uint _vdg = (_vg & -32);
  char _vdh = (_v5e != _vdg);
  if (_vdh) {
    _vdf = 8;
  } else {
    _vdf = 0;
  }
  uint _vdi = (_vde | _vdf);
  uint _vdj;
  char _vdk = (_v6q != _vd6);
  if (_vdk) {
    _vdj = 16;
  } else {
    _vdj = 0;
  }
  uint _vdl = (_vdi | _vdj);
  uint _vdm;
  uint _vdn = (_vg & -8);
  char _vdo = (_v83 != _vdn);
  if (_vdo) {
    _vdm = 32;
  } else {
    _vdm = 0;
  }
  uint _vdp = (_vdl | _vdm);
  uint _vdq;
  uint _vdr;
  char _vds = (_vg < 16);
  if (_vds) {
    _vdr = 0;
  } else {
    _vdr = _vg;
  }
  char _vdt = (_v9c != _vdr);
  if (_vdt) {
    _vdq = 64;
  } else {
    _vdq = 0;
  }
  uint _vdu = (_vdp | _vdq);
  uint _vdv;
  char _vdw = (_vad != 1);
  if (_vdw) {
    _vdv = 128;
  } else {
    _vdv = 0;
  }
  uint _vdx = (_vdu | _vdv);
  uint _vdy;
  char _vdz = (!_vbd);
  if (_vdz) {
    _vdy = 256;
  } else {
    _vdy = 0;
  }
  uint _ve0 = (_vdx | _vdy);
  uint _ve1;
  char _ve2 = (!_vce);
  if (_ve2) {
    _ve1 = 512;
  } else {
    _ve1 = 0;
  }
  uint _ve3 = (_ve0 | _ve1);
  char _ve4 = (_v3 < 32);
  if (_ve4) {
    ulong _ve5 = ((ulong) _v3);
    ulong _ve6 = (_ve5 * 4);
    ulong _ve7 = ((ulong) (*_v1).f.result);
    ulong _ve8 = (_ve7 + _ve6);
    global uint* _ve9 = ((global uint*) _ve8);
    ulong _vea = ((ulong) _ve9);
    global char* _veb = &(_v0 /*char*/)[_vea];
    global uint* _vec = ((global uint*) _veb);
    _vec[0] = _ve3;
  }
  return /*void*/;
}
