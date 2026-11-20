#!/usr/bin/env python3
"""Probe v_cmpx_* flag writes on a gfx1036 device and tabulate them as cmpxFlagTables.scala.

usage: python3 -I cmpx_flag_probe.py assemble <workdir>
       hipcc --offload-arch=gfx1036 -o <workdir>/probe <workdir>/probe.hip -lamdhip64
       <workdir>/probe > <workdir>/hw-run.log
       python3 -I cmpx_flag_probe.py tabulate <workdir> <output.scala>

assemble needs llvm-mc; the run needs a gfx1036 device. cmpx-forms.txt beside this script lists the probed mnemonics.
"""
import os
import re
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
DEAD = 0xdeadbeef
SENT = f'0x{DEAD:x}'
STRIDE = 38
EDGE = [
    0x0000000000000000, 0x8000000000000000, 0x0000000000000001, 0xffffffffffffffff,
    0x3ff0000000000000, 0xbff0000000000000, 0x4000000000000000, 0x3fe0000000000000,
    0x7ff0000000000000, 0xfff0000000000000, 0x7ff8000000000000, 0x7ff0000000000001,
    0x000fffffffffffff, 0x7fffffffffffffff, 0x8000000000000001, 0x0000000000000003,
    0x000000003f800000, 0x00000000bf800000, 0x000000007f800000, 0x00000000ff800000,
    0x000000007fc00000, 0x0000000000800000, 0x000000007f7fffff, 0x00000000ffff0000,
    0x000000000000ffff, 0x0000000080000000, 0x000000007fffffff, 0x00000000ffffffff,
    0x0000000100000000, 0x00000001ffffffff, 0x0000000040000000, 0x00000000c0000000,
]
ROWS = [(0xffffffff, 0), (0x0, 0), (0x1, 1), (0x80000000, 5), (0x55555555, 9),
        (0xaaaaaaaa, 13), (0x0000ffff, 17), (0xf00f0ff0, 23)]
OPERANDS = [('v2', 'v6'), ('v2', 'v[6:7]'), ('v[2:3]', 'v6'), ('v[2:3]', 'v[6:7]')]
PROGRAM = [
    's_mov_b64 s[18:19], exec',
    f'v_mov_b32 v10, {SENT}',
    f's_mov_b32 s14, {SENT}',
    f's_mov_b32 s15, {SENT}',
    f's_mov_b32 vcc_lo, {SENT}',
    f's_mov_b32 vcc_hi, {SENT}',
    'v_mov_b32 v2, %8',
    'v_mov_b32 v3, %9',
    'v_mov_b32 v6, %10',
    'v_mov_b32 v7, %11',
    's_mov_b32 exec_lo, %7',
    's_nop 0',
    'BYTES',
    's_nop 7',
    's_mov_b32 s20, exec_lo',
    's_mov_b32 s21, exec_hi',
    's_mov_b32 %5, vcc_lo',
    's_mov_b32 %6, vcc_hi',
    'v_mov_b32 v10, 1',
    's_nop 7',
    's_mov_b64 exec, s[18:19]',
    's_nop 4',
    's_mov_b32 %0, s14',
    's_mov_b32 %1, s15',
    's_mov_b32 %2, s20',
    's_mov_b32 %3, s21',
    'v_mov_b32 %4, v10',
]
CLOBBERS = ['"scc"', '"vcc"', '"s14"', '"s15"', '"s18"', '"s19"', '"s20"', '"s21"',
            '"v2"', '"v3"', '"v6"', '"v7"', '"v10"']


def assemble(workdir):
    os.makedirs(workdir, exist_ok=True)
    with open(os.path.join(HERE, 'cmpx-forms.txt')) as fh:
        names = [line.strip() for line in fh if line.strip()]
    cands = [f'{n} {a}, {b}' for n in names for a, b in OPERANDS]
    path = os.path.join(workdir, 'cands.s')
    with open(path, 'w') as fh:
        fh.write('\n'.join(cands) + '\n')
    res = subprocess.run(['llvm-mc', '-triple=amdgcn-amd-amdhsa', '-mcpu=gfx1036', '-show-encoding', path],
                         capture_output=True, text=True)
    errs = {int(m.group(1)) for m in re.finditer(r'cands\.s:(\d+):\d+: error', res.stderr)}
    encs = re.findall(r'encoding: \[([^\]]*)\]', res.stdout)
    good = [i + 1 for i in range(len(cands)) if (i + 1) not in errs]
    assert len(good) == len(encs), (len(good), len(encs))
    by_line = dict(zip(good, encs))
    chosen = []
    for index, name in enumerate(names):
        line_nos = [index * len(OPERANDS) + j + 1 for j in range(len(OPERANDS))]
        pick = next((k for k in line_nos if k in by_line), None)
        if pick is None:
            raise SystemExit(f'{name}: no operand choice assembles')
        chosen.append((name, cands[pick - 1], [int(x, 16) for x in by_line[pick].split(',')]))
    assert all(len(b) == (8 if n.endswith('_e64') else 4) for n, _, b in chosen)
    assert all(b[0] == 0x7e for n, _, b in chosen if n.endswith('_e64'))
    with open(os.path.join(workdir, 'forms.tsv'), 'w') as fh:
        for n, text, bts in chosen:
            fh.write(f"{n}\t{text}\t{' '.join(f'{b:02x}' for b in bts)}\n")
    variants = []
    for n, text, bts in chosen:
        variants.append((n, 'null', text, bts))
        if n.endswith('_e64'):
            variants.append((n, 's14', text, [14] + bts[1:]))
    print('cmpx forms', len(names), 'chosen', len(chosen))
    write_probe(workdir, variants)


def write_probe(workdir, variants):
    o = [
        '#include <hip/hip_runtime.h>',
        '#include <cstdint>',
        '#include <cstdio>',
        '#include <vector>',
        '',
        '__global__ void wave_probe(uint32_t* o) {',
        '  const uint64_t e = __builtin_amdgcn_read_exec();',
        '  if (threadIdx.x == 0) { o[0] = (uint32_t)e; o[1] = (uint32_t)(e >> 32); }',
        '}',
        '',
        '__global__ void probe(const uint32_t* in, uint32_t* out, int form) {',
        '  const int row = blockIdx.x;',
        '  const int lane = threadIdx.x;',
        '  const uint32_t mask = (uint32_t)__builtin_amdgcn_readfirstlane((int)in[row]);',
        '  const int j = (lane + (int)in[8 + row]) & 31;',
        '  const uint32_t xl = in[16 + 2 * lane], xh = in[17 + 2 * lane];',
        '  const uint32_t yl = in[16 + 2 * j], yh = in[17 + 2 * j];',
        f'  uint32_t* r = out + {STRIDE} * row;',
        '  uint32_t s14o = 0, s15o = 0, elo = 0, ehi = 0, vlo = 0, vhi = 0, mk = 0;',
        '  switch (form) {',
    ]
    for idx, (name, variant, text, bts) in enumerate(variants):
        byte_text = ', '.join(f'0x{b:02x}' for b in bts)
        lines = [f'.byte {byte_text}' if line == 'BYTES' else line for line in PROGRAM]
        asm_text = '\\n\\t'.join(lines)
        o += [
            f'    case {idx}: {{ // {name} {variant} {text}',
            f'      asm volatile("{asm_text}"',
            '        : "=s"(s14o), "=s"(s15o), "=s"(elo), "=s"(ehi), "=v"(mk), "=s"(vlo), "=s"(vhi)',
            '        : "s"(mask), "v"(xl), "v"(xh), "v"(yl), "v"(yh)',
            '        : ' + ', '.join(CLOBBERS) + ');',
            '      break;',
            '    }',
        ]
    o += [
        '    default: break;',
        '  }',
        '  r[6 + lane] = mk;',
        '  if (lane == 0) { r[0] = s14o; r[1] = s15o; r[2] = elo; r[3] = ehi; r[4] = vlo; r[5] = vhi; }',
        '}',
        '',
        'int main() {',
    ]
    edge_words = ', '.join(f'0x{v & 0xffffffff:x}u, 0x{v >> 32:x}u' for v in EDGE)
    masks = ', '.join(f'0x{m:x}u' for m, _ in ROWS)
    rots = ', '.join(f'{r}u' for _, r in ROWS)
    o += [
        f'  uint32_t hin[16 + 2 * {len(EDGE)}] = {{{masks}, {rots}, {edge_words}}};',
        f'  const int nforms = {len(variants)}, nrows = {len(ROWS)};',
        '  hipDeviceProp_t prop;',
        '  hipGetDeviceProperties(&prop, 0);',
        '  printf("warpSize %d arch %s\\n", prop.warpSize, prop.gcnArchName);',
        '  uint32_t *din, *dout, *wout;',
        '  hipMalloc(&din, sizeof(hin));',
        f'  hipMalloc(&dout, sizeof(uint32_t) * {STRIDE} * nrows);',
        '  hipMalloc(&wout, sizeof(uint32_t) * 2);',
        '  hipMemcpy(din, hin, sizeof(hin), hipMemcpyHostToDevice);',
        '  hipLaunchKernelGGL(wave_probe, dim3(1), dim3(32), 0, 0, wout);',
        '  uint32_t we[2];',
        '  hipMemcpy(we, wout, sizeof(we), hipMemcpyDeviceToHost);',
        '  printf("launch exec %08x %08x\\n", we[0], we[1]);',
        f'  std::vector<uint32_t> host({STRIDE} * nrows);',
        '  for (int f = 0; f < nforms; f++) {',
        f'    hipMemset(dout, 0, sizeof(uint32_t) * {STRIDE} * nrows);',
        '    hipLaunchKernelGGL(probe, dim3(nrows), dim3(32), 0, 0, din, dout, f);',
        '    hipError_t e = hipDeviceSynchronize();',
        '    if (e != hipSuccess) { printf("form %d sync %d\\n", f, (int)e); continue; }',
        '    hipMemcpy(host.data(), dout, sizeof(uint32_t) * host.size(), hipMemcpyDeviceToHost);',
        '    for (int r = 0; r < nrows; r++) {',
        f'      const uint32_t* v = &host[{STRIDE} * r];',
        '      uint32_t marks = 0;',
        '      for (int l = 0; l < 32; l++) marks |= (uint32_t)(v[6 + l] == 1u) << l;',
        '      printf("%d %d %08x %08x %08x %08x %08x %08x %08x\\n", f, r, v[0], v[1], v[2], v[3], v[4], v[5], marks);',
        '    }',
        '  }',
        '  return 0;',
        '}',
    ]
    with open(os.path.join(workdir, 'probe.hip'), 'w') as fh:
        fh.write('\n'.join(o) + '\n')
    with open(os.path.join(workdir, 'names.txt'), 'w') as fh:
        fh.write('\n'.join(f'{i} {n} {v} {t}' for i, (n, v, t, _) in enumerate(variants)) + '\n')


def tabulate(workdir, output):
    names = {}
    with open(os.path.join(workdir, 'names.txt')) as fh:
        for line in fh:
            idx, name, variant, text = line.rstrip('\n').split(' ', 3)
            names[int(idx)] = (name, variant, text)
    code = {}
    with open(os.path.join(workdir, 'forms.tsv')) as fh:
        for line in fh:
            name, _, hexes = line.rstrip('\n').split('\t')
            code[name] = [int(x, 16) for x in hexes.split()]
    hw = {}
    with open(os.path.join(workdir, 'hw-run.log')) as fh:
        for line in fh:
            p = line.split()
            if len(p) != 9 or not p[0].isdigit():
                continue
            s14, s15, elo, ehi, vlo, vhi, marks = (int(x, 16) for x in p[2:])
            assert (s14, s15, ehi, vhi) == (DEAD, DEAD, 0, DEAD), line
            assert marks == elo, line
            hw[(int(p[0]), int(p[1]))] = (elo, vlo)
    assert len(hw) == len(names) * len(ROWS), (len(hw), len(names))
    forms = []
    for idx, (name, variant, text) in sorted(names.items()):
        if variant != 'null':
            continue
        pairs = [hw[(idx, r)] for r in range(len(ROWS))]
        for k, (other, other_variant, _) in names.items():
            if other == name and other_variant == 's14':
                assert [hw[(k, r)] for r in range(len(ROWS))] == pairs, text
        forms.append((text, code[name], pairs))
    out = [
        'package net.kurobako.gemstone.isa.amdgpu',
        '',
        'import net.kurobako.gemstone.collection.*',
        '',
        'object cmpxFlagTables {',
        '  case class CmpxForm(text: String, code: Vec[Int], execs: String, vccs: String)',
        '',
        '  val edge: Vec[Long] = Vec(',
    ]
    out += [f'    0x{v:016x}L{"," if i + 1 < len(EDGE) else ""}' for i, v in enumerate(EDGE)]
    out += ['  )', '', '  val masks: Vec[(Long, Long)] = Vec(']
    out += [f'    (0x{m:x}L, {r}L){"," if i + 1 < len(ROWS) else ""}' for i, (m, r) in enumerate(ROWS)]
    out += ['  )', '', '  val forms: Vec[CmpxForm] = Vec(']
    for i, (text, code_bytes, pairs) in enumerate(forms):
        out += [
            '    CmpxForm(',
            f'      "{text}",',
            '      Vec(' + ', '.join(f'0x{b:02x}' for b in code_bytes) + '),',
            '      "' + ' '.join(f'{e:08x}' for e, _ in pairs) + '",',
            '      "' + ' '.join(f'{v:08x}' for _, v in pairs) + '"',
            '    )' + (',' if i + 1 < len(forms) else ''),
        ]
    out += ['  )', '}', '']
    with open(output, 'w') as fh:
        fh.write('\n'.join(out))
    print('forms', len(forms), 'rows', len(ROWS), 'out', output)


if __name__ == '__main__':
    if len(sys.argv) == 3 and sys.argv[1] == 'assemble':
        assemble(sys.argv[2])
    elif len(sys.argv) == 4 and sys.argv[1] == 'tabulate':
        tabulate(sys.argv[2], sys.argv[3])
    else:
        sys.exit(__doc__)
