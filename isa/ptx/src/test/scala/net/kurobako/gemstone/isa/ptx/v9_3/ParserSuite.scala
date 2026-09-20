package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Vec

final class ParserSuite extends munit.FunSuite {
  private val modules = Vec(
    "// comment\n.version 9.3\n.target sm_100, texmode_independent\n.address_size 64",
    ".extern .func __assertfail(.param .b64 message);",
    ".weak .entry kernel(.param .align 8 .b8 value[24], .param .u64 pointer);",
    ".visible .entry kernel(.param .u64 pointer) .maxntid 256, 1, 1 { " +
      ".reg .b32 %r<2>; again: mov.u32 %r0, %tid.x; ret; }",
    ".global .align 4 .b8 table[4] = {65, 144, 67, 60};",
    ".extern .shared .align 16 .b8 shmem[];",
    "{ cvt.rn.bf16.f32 %rs13, %f17; mov.b16 c, 0x3f80U; fma.rn.bf16 %rs17, %rs13, c, %rs16; }",
    "@p ld.global.nc.L1::evict_last.v2.f64 {%f0,%f1}, [%rd3+8];",
    "tcgen05.mma.ws.cta_group::1.kind::i8.collector::b2:use [d], [a], b, idesc, p;",
    ".target sm_100, texmode_independent;\n.file 1 \"a\\\"b.ptx\";",
    "cp.async.bulk.tensor.2d.shared::cluster.global.mbarrier::complete_tx::bytes " +
      "[dst], [tmap, {x, y}], [mbar];",
    "shfl.sync.up.b32 Ry|p, -Rx, 0x1, 0x1f, 0xffffffff;"
  )

  modules.zipWithIndex.foreach { case (source, index) =>
    test(s"parses representative module $index") {
      assert(Parser.parse(source).isRight, Parser.parse(source).left.toString)
    }
  }

  test("parses a large generated module") {
    val source = StringBuilder(".version 8.0\n.target sm_80\n.address_size 64\n")
    (0 until 256).foreach { index =>
      source.append(s".visible .entry kernel_$index() { .reg .b32 %r<2>; mov.u32 %r0, %tid.x; ")
      (0 until 64).foreach(value => source.append(s"add.u32 %r1, %r0, ${value + 1}; "))
      source.append("ret; }\n")
    }
    assert(Parser.parse(source.result()).isRight)
  }
}
