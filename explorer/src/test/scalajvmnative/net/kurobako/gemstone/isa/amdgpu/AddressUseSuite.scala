package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.RegisterUse
import scala.concurrent.duration.*

/** The reads [[RegisterUses]] names as the address of each instruction that reaches memory, over every instance the
  * explorer writes, on the first target of each ISA that has scheduling data.
  */
class AddressUseSuite extends Suite {
  override def baseTimeout: FiniteDuration = 15.minutes

  private val memoryTraits = Set("smrd", "vmem", "store")

  private def uses(target: Target, text: String): Either[String, (VMIR.Instruction, RegisterUse, RegisterUse)] = for {
    program     <- asm.Parser.parse(text).left.map(_.diagnostic.raw)
    code        <- asm.Assembler.encode(program, TargetMode(target)).left.map(_.toString)
    instruction <- VMCompiler.decodeInstruction(code, target).left.map(_.raw)
  } yield {
    val plain = RegisterUses.of(instruction, TargetDefaults.waveSize(target))
    val state = RegisterUses.state(
      instruction,
      CodeStream(Vec.toArray(code), TargetMode(target)),
      0,
      TargetDefaults.waveSize(target),
      target.isa
    )
    (instruction, plain, plain.merge(state))
  }

  private def named(target: String, text: String): RegisterUse =
    Target.all.find(_.raw == target).toRight(s"no target $target").flatMap(uses(_, text)).fold(fail(_), _._3)

  private def sorted(names: Vec[String]): Seq[String] = names.toSeq.sorted

  test("a store names its address registers and leaves the stored value out") {
    assertEquals(
      sorted(named("gfx1036", "global_store_dwordx4 v0, v[1:4], s[16:17]").address),
      Seq("s[16:17]", "v0")
    )
    assertEquals(sorted(named("gfx1036", "global_store_dword v[0:1], v2, off").address), Seq("v[0:1]"))
    assertEquals(sorted(named("gfx906", "ds_write_b32 v1, v2").address), Seq("v1"))
    assertEquals(sorted(named("gfx906", "buffer_store_dword v1, v2, s[4:7], 0 offen").address), Seq("s[4:7]", "v2"))
  }

  test("a load names its address registers and leaves the preserved half out") {
    assertEquals(sorted(named("gfx906", "global_load_dword v1, v0, s[2:3]").address), Seq("s[2:3]", "v0"))
    assertEquals(sorted(named("gfx1036", "s_load_dword s9, s[0:1], 0x0").address), Seq("s[0:1]"))
    assertEquals(sorted(named("gfx906", "global_load_short_d16_hi v1, v[2:3], off").address), Seq("v[2:3]"))
  }

  test("an atomic names its address and not its data") {
    assertEquals(sorted(named("gfx906", "global_atomic_add v[1:2], v3, off").address), Seq("v[1:2]"))
  }

  test("an instruction that does not reach memory names no address") {
    assertEquals(named("gfx1036", "v_add_nc_u32 v1, v2, v3").address, Vec.empty[String])
  }

  private case class Checked(label: String, outside: Boolean, bare: Boolean)

  private def waits(instruction: VMIR.Instruction): Boolean = instruction match {
    case _: VMIR.Instruction.WaitCounts => true
    case _                              => false
  }

  test("every instruction that reaches memory names an address within its reads") {
    val checked = Target.all.groupBy(_.isa).toVec.sortBy(_._1.toString).map(_._2.head).flatMap(target =>
      Vec.from(AMDGPUModel.entries(Some(target.raw))).flatMap(entry =>
        Vec.from(entry.forms).flatMap(form =>
          Vec.from(AMDGPUModel.instances(Some(target.raw), entry.mnemonic, form)).flatMap { (instanceTarget, text, _) =>
            val lds     = text.split("\\s+").contains("lds")
            val reaches =
              Scheduling.timing(instanceTarget, entry.mnemonic, lds).exists(_.traits.exists(memoryTraits))
            Vec.from(Option.when(reaches)(uses(instanceTarget, text)).flatMap(_.toOption)).map {
              (instruction, plain, merged) =>
                Checked(
                  s"${instanceTarget.raw} $text",
                  outside = !merged.address.forall(merged.reads.contains(_)),
                  bare = !waits(instruction) && plain.address.isEmpty && plain.reads.nonEmpty
                )
            }
          }
        )
      )
    )
    val bare    = checked.filter(_.bare).map(_.label)
    val outside = checked.filter(_.outside).map(_.label)
    println(
      s"address use: ${checked.size} memory instances, ${bare.size} without an address, ${outside.size} outside reads"
    )
    assert(checked.nonEmpty, "no instance that reaches memory was checked")
    assert(outside.isEmpty, outside.take(20).mkString("\n"))
    assert(bare.isEmpty, bare.take(20).mkString("\n"))
  }
}
