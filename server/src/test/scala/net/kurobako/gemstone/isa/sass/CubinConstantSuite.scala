package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class CubinConstantSuite extends munit.FunSuite {
  for {
    (sm, text) <- Vec(35 -> ConstantFixtures.constantSm35, 89 -> ConstantFixtures.constantSm89)
    destination = if (sm == 35) "R4" else "R5"
    index       = if (sm == 35) "R5" else "R0"
    original    = s"LDC $destination, c[0x3][$index]"
    (access, instruction, selected) <- Vec(
      ("dynamic", original, None),
      ("static zero", s"LDC $destination, c[0x3][0x0]", Some(0)),
      ("static offset", s"LDC $destination, c[0x3][0x4]", Some(1)),
      ("zero register offset", s"LDC $destination, c[0x3][RZ+0x4]", Some(1)),
      ("constant operand", s"MOV $destination, c[0x3][0x4]", Some(1))
    )
  }
    test(s"sm_$sm $access constant reads observe bound storage updates") {
      val initial = LittleEndian.toVec(3L, 4) ++ LittleEndian.toVec(4L, 4)
      val base    = SASSFixture.module(
        sm,
        Vec(SASSFixture.Kernel(
          "constant_read",
          if (sm == 35) 0x140 else 0x160,
          Vec(0 -> 8),
          registers = if (sm == 35) 6 else 8
        )),
        constants = Vec(".nv.constant3" -> initial)
      )
      val bankIndex = base.sections.indexWhere(_.name == ".nv.constant3")
      val module    = base.copy(globals = Vec(Cubin.Global("values", initial, Some(bankIndex))))
      val functions = ListingDecoder.parse(
        text.replace(original, instruction),
        "constant_read",
        sm
      ).fold(fail(_), identity)
      val lowered = CubinBackendFixture.lower(module, module.kernels.head, functions).fold(fail(_), identity)
      CubinBackendFixture.session { (service, fixtures) =>
        val catalog =
          service.load(CubinBackendFixture.encoding, fixtures.image(lowered)).fold(e => fail(e.message), identity)
        assert(service.symbols(catalog).toOption.get.globals.exists(_.name == ("values": NonEmpty[String])))
        val named = service.global(catalog, "values": NonEmpty[String]).fold(e => fail(e.message), identity)
        assertEquals(named.size.raw, 8L)
        assertEquals(named.initial.size, 0)
        val alias = named.alias.getOrElse(fail("named constant has no bank alias"))
        val bank  = service.global(catalog, alias.name).fold(e => fail(e.message), identity)
        assert(alias.offset.raw + named.size.raw <= bank.size.raw)
        val storage = service.registerAllocation(bank.size.raw.unsafe[Long > 0], AllocationKind.Shared).toOption.get
        assert(service.write(storage, 0L, bank.initial).isRight)
        val output  = service.registerAllocation(8L.unsafe[Long > 0], AllocationKind.Shared).toOption.get
        val program = service.load(CubinBackendFixture.encoding, fixtures.image(lowered))
          .fold(e => fail(e.message), identity)
        assert(service.bindGlobal(program, alias.name, storage, 0L).isRight)
        val task = KernelTask.validated(
          "constant_read": NonEmpty[String],
          LaunchGeometry.Dimensions(2, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(KernelTask.Argument.Memory(output, 0L))
        ).toOption.get
        for (values <- Vec(Vec(17, 29), Vec(31, 47))) {
          val bytes = Vec.from(values).flatMap(n => LittleEndian.toVec(n.toLong, 4))
          assert(service.write(storage, alias.offset, bytes).isRight)
          val completion = service.launch(program, task).fold(e => fail(e.message), identity)
          assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
          val expected = selected.fold(values)(index => Vec.fill(2)(values(index)))
            .flatMap(value => LittleEndian.toVec(value.toLong, 4))
          assertEquals(service.read(output, 0L, 8), Right(expected))
        }
      }
    }
}
