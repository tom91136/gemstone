extern "C" __global__ void intchain(unsigned *out, unsigned a, unsigned b, unsigned c) {
  unsigned x = a + b;
  x = x * c + a;
  x = (x << 3) ^ b;
  x = (x >> 5) | c;
  x = x * x + 7u;
  x = (x & 0xff00ffu) + (a - c);
  out[0] = x;
}

extern "C" __global__ void intwide(unsigned long long *out, unsigned long long a, unsigned long long b) {
  unsigned long long x = a + b;
  x = x * b + a;
  x = (x << 7) ^ (a >> 3);
  x = x - b * 3ull;
  out[0] = x;
}

extern "C" __global__ void intfan(unsigned *out, unsigned a, unsigned b, unsigned c, unsigned d) {
  unsigned p = a * b;
  unsigned q = c * d;
  unsigned r = a + d;
  unsigned s = b ^ c;
  out[0] = p + q;
  out[1] = r * s;
  out[2] = (p ^ r) + (q & s);
  out[3] = __popc(p) + __clz(q) + __brev(r);
}
