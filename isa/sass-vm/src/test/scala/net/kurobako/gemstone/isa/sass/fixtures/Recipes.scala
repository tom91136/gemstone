package net.kurobako.gemstone.isa.sass.fixtures

import java.nio.file.Files
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.BarrierFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.CompilerKernelFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.ConstantFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.ControlFlowFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.ConversionFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.FloatFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.GenericMemoryFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.HalfFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.IntegerAddFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.IntegerCompareFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.IntegerMultiplyFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.LeaFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.LogicFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.MemoryFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.MinMaxFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.SharedMemoryFixtures.*
import net.kurobako.gemstone.isa.sass.fixtures.TensorFixtures.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.testing.*
import scala.collection.mutable

/** Reproduces every fixture listing with the NVIDIA toolkit that produced it. A listing compiled for several
  * architectures has one recipe per architecture. Patched listings disassemble deliberately edited cubins.
  */
class Recipes extends NvidiaFixtureSuite {
  private type Tools = (ptxas: String, nvdisasm: Option[String])
  private val Cuda11: Tools   = (ptxas = "11.8.89", nvdisasm = Some("11.8.86"))
  private val Cuda13: Tools   = (ptxas = "13.3.73", nvdisasm = Some("13.3.73"))
  private val Cuda1178: Tools = (ptxas = "11.8.89", nvdisasm = Some("13.2.78"))
  private val Cuda1378: Tools = (ptxas = "13.3.73", nvdisasm = Some("13.2.78"))

  private def toolkit(sm: Int): Tools = if (sm <= 70) Cuda11 else Cuda13

  private val ptxSources = mutable.LinkedHashMap.empty[String, String]

  /** Every distinct PTX input, named after the first listing compiled from it. */
  def ptxInputs: Vec[(String, String)] = Vec.from(ptxSources.map(_.swap))

  private type Source = (file: String, text: String)
  private def cuda(text: String): Source = (file = "kernel.cu", text = text)
  private def ptx(text: String): Source  = (file = "kernel.ptx", text = text)

  private def compile(source: Source, sm: Int, flags: String): String =
    if (source.file.endsWith(".cu"))
      s"nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_$sm $flags ${source.file} -o kernel.cubin"
    else s"ptxas -arch=sm_$sm $flags ${source.file} -o kernel.cubin"

  private def listing(listing: String, sm: Int, tools: Tools, inputs: Vec[(String, String)], output: String)(
      script: Vec[String]
  )(using munit.Location): Unit = {
    inputs.foreach((file, text) => if (file.endsWith(".ptx") && !ptxSources.contains(text)) ptxSources(text) = listing)
    verifies(s"$listing sm_$sm")(tools, inputs) { (directory, run) =>
      run(script)
      assertEquals(Fixture.normalise(Files.readString(directory.resolve(output))), Fixture.listing(listing))
    }
  }

  /** Compiles `source`, optionally selects one function by symbol name, and disassembles with `nvdisasm -c -ndf`. */
  private def recipe(
      name: String,
      source: Source,
      sm: Int,
      function: String = "",
      hex: Boolean = false,
      flags: String = "",
      tools: Option[Tools] = None
  )(using munit.Location): Unit = {
    val select = Vec(
      s"""symbol=$$(readelf -sW kernel.cubin | awk '$$NF == "$function" {sub(":", "", $$1); print $$1; exit}')""",
      "test -n \"$symbol\""
    )
    val options = (if (hex) " -hex" else "") + (if (function.isEmpty) "" else " -fun \"$symbol\"")
    listing(name, sm, tools.getOrElse(toolkit(sm)), Vec(source), "output.sass")(
      Vec(compile(source, sm, flags)) ++ (if (function.isEmpty) Vec.empty[String] else select) :+
        s"nvdisasm -c -ndf$options kernel.cubin > output.sass"
    )
  }

  /** Runs a custom script, typically one that edits the compiled cubin, against `file -> text` inputs. */
  private def patched(
      name: String,
      sm: Int,
      inputs: Vec[(String, String)],
      output: String,
      tools: Option[Tools] = None
  )(
      script: String*
  )(using munit.Location): Unit = listing(name, sm, tools.getOrElse(toolkit(sm)), inputs, output)(Vec.from(script))

  /** Checks a PTX source that nvcc generated from a CUDA source. */
  private def generated(name: String, output: String, source: String, sm: Int)(using munit.Location): Unit =
    verifies(name)(toolkit(sm), Vec("kernel.cu" -> source)) { (directory, run) =>
      run(Vec(s"nvcc -ccbin /usr/bin/g++-11 -ptx -arch=sm_$sm kernel.cu -o output.ptx"))
      assertEquals(Files.readString(directory.resolve("output.ptx")).replaceAll("\n+\\z", "\n"), output)
    }

  recipe("arithmeticSm35", cuda(arithmeticCuda), 35)
  recipe("atomicAddSm86", cuda(atomicAddCuda), 86)
  recipe("bitfieldSm35", cuda(bitfieldCuda), 35)
  recipe("bmovClearSm70", cuda(bmovClearCuda), 70, hex = true)
  recipe("bmovClearSm75", cuda(bmovClearCuda), 75)
  recipe("cacheInvalidateSm35", ptx(cacheInvalidatePtx), 35, hex = true)
  recipe("callSm35", cuda(callCuda), 35)
  recipe("callSm50", cuda(callCuda), 50)
  recipe("callSm60", cuda(callCuda), 60)
  recipe("callIndirectSm35", cuda(callIndirectCuda), 35)
  recipe("constantSm35", cuda(constantCuda), 35)
  recipe("constantSm89", cuda(constantCuda), 89)
  recipe("copyAddSm89", ptx(copyAddPtx), 89, hex = true)
  recipe("divideSm89", cuda(divideCuda), 89)
  recipe("dmmaSm80", ptx(dmmaPtx), 80)
  recipe("dsetSm35", ptx(dsetPtx), 35, hex = true)
  recipe("extremaSm89", ptx(extremaPtx), 89)
  recipe("f2fF16F32Sm70", cuda(f2fF16F32Cuda), 70, hex = true)
  recipe("f2fF16F32Sm75", cuda(f2fF16F32Cuda), 75, hex = true)
  recipe("f2fF16F32Sm89", cuda(f2fF16F32Cuda), 89, hex = true)
  recipe("f2fF32F64Sm70", ptx(f2fF32F64Ptx(70)), 70, hex = true)
  recipe("f2fF32F64Sm75", ptx(f2fF32F64Ptx(75)), 75, hex = true)
  recipe("f2fF32F64Sm80", ptx(f2fF32F64Ptx(80)), 80)
  recipe("f2fF32F64Sm86", ptx(f2fF32F64Ptx(86)), 86)
  recipe("f2fF32F64Sm89", ptx(f2fF32F64Ptx(89)), 89)
  recipe("f2fpPackAbSm89", cuda(f2fpPackAbCuda), 89, hex = true)
  recipe("fmnmxSm86", ptx(fmnmxPtx), 86, hex = true)
  recipe("frndSm70", ptx(frndPtx(70)), 70, hex = true)
  recipe("frndSm75", ptx(frndPtx(75)), 75, hex = true)
  recipe("frndSm80", ptx(frndPtx(80)), 80, hex = true)
  recipe("frndSm86", ptx(frndPtx(86)), 86, hex = true)
  recipe("frndSm89", ptx(frndPtx(89)), 89, hex = true)
  recipe("fselImmediateSm86", ptx(fselImmediatePtx), 86, hex = true)
  recipe("genericAddressGlobalLoadSm50", ptx(genericAddressPtx), 50, "global_load", hex = true)
  recipe("genericAddressGlobalLoadSm60", ptx(genericAddressPtx), 60, "global_load", hex = true)
  recipe("genericAddressGlobalStoreSm50", ptx(genericAddressPtx), 50, "global_store", hex = true)
  recipe("genericAddressGlobalStoreSm60", ptx(genericAddressPtx), 60, "global_store", hex = true)
  recipe("genericAddressLocalLoadSm50", ptx(genericAddressPtx), 50, "local_load", hex = true)
  recipe("genericAddressLocalLoadSm60", ptx(genericAddressPtx), 60, "local_load", hex = true)
  recipe("genericAddressLocalStoreSm50", ptx(genericAddressPtx), 50, "local_store", hex = true)
  recipe("genericAddressLocalStoreSm60", ptx(genericAddressPtx), 60, "local_store", hex = true)
  recipe("genericAddressSharedLoadSm50", ptx(genericAddressPtx), 50, "shared_load", hex = true)
  recipe("genericAddressSharedLoadSm60", ptx(genericAddressPtx), 60, "shared_load", hex = true)
  recipe("genericAddressSharedStoreSm50", ptx(genericAddressPtx), 50, "shared_store", hex = true)
  recipe("genericAddressSharedStoreSm60", ptx(genericAddressPtx), 60, "shared_store", hex = true)
  recipe("genericByteGlobalS8Sm50", ptx(genericBytePtx), 50, "global_s8", hex = true)
  recipe("genericByteGlobalS8Sm60", ptx(genericBytePtx), 60, "global_s8", hex = true)
  recipe("genericByteGlobalU8BarrierSm50", ptx(genericBytePtx), 50, "global_u8_barrier", hex = true)
  recipe("genericByteGlobalU8BarrierSm60", ptx(genericBytePtx), 60, "global_u8_barrier", hex = true)
  recipe("genericByteGlobalU8Sm50", ptx(genericBytePtx), 50, "global_u8", hex = true)
  recipe("genericByteGlobalU8Sm60", ptx(genericBytePtx), 60, "global_u8", hex = true)
  recipe("genericByteLocalU8Sm50", ptx(genericBytePtx), 50, "local_u8", hex = true)
  recipe("genericByteLocalU8Sm60", ptx(genericBytePtx), 60, "local_u8", hex = true)
  recipe("genericByteSharedU8Sm50", ptx(genericBytePtx), 50, "shared_u8", hex = true)
  recipe("genericByteSharedU8Sm60", ptx(genericBytePtx), 60, "shared_u8", hex = true)
  recipe("genericCompoundGlobalSm50", ptx(genericCompoundPtx), 50, "compound_global", hex = true)
  recipe("genericCompoundGlobalSm60", ptx(genericCompoundPtx), 60, "compound_global", hex = true)
  recipe("genericCompoundLocalSm50", ptx(genericCompoundPtx), 50, "compound_local", hex = true)
  recipe("genericCompoundLocalSm60", ptx(genericCompoundPtx), 60, "compound_local", hex = true)
  recipe("genericCompoundSharedSm50", ptx(genericCompoundPtx), 50, "compound_shared", hex = true)
  recipe("genericCompoundSharedSm60", ptx(genericCompoundPtx), 60, "compound_shared", hex = true)
  recipe("genericLoadV2U32Sm50", ptx(genericVectorPtx), 50, "load_v2_u32", hex = true)
  recipe("genericLoadV2U32Sm60", ptx(genericVectorPtx), 60, "load_v2_u32", hex = true)
  recipe("genericLoadV2U64Sm50", ptx(genericVectorPtx), 50, "load_v2_u64", hex = true)
  recipe("genericLoadV2U64Sm60", ptx(genericVectorPtx), 60, "load_v2_u64", hex = true)
  recipe("genericLoadV4U32Sm50", ptx(genericVectorPtx), 50, "load_v4_u32", hex = true)
  recipe("genericLoadV4U32Sm60", ptx(genericVectorPtx), 60, "load_v4_u32", hex = true)
  recipe("genericRepeatedStoreThenLoadSm50", ptx(genericVectorPtx), 50, "repeated_store_then_load", hex = true)
  recipe("genericRepeatedStoreThenLoadSm60", ptx(genericVectorPtx), 60, "repeated_store_then_load", hex = true)
  recipe("genericStoreV2U32Sm50", ptx(genericVectorPtx), 50, "store_v2_u32", hex = true)
  recipe("genericStoreV2U32Sm60", ptx(genericVectorPtx), 60, "store_v2_u32", hex = true)
  recipe("genericStoreV2U64Sm50", ptx(genericVectorPtx), 50, "store_v2_u64", hex = true)
  recipe("genericStoreV2U64Sm50", ptx(genericVectorPtx), 60, "store_v2_u64", hex = true)
  recipe("genericStoreV4U32Sm50", ptx(genericVectorPtx), 50, "store_v4_u32", hex = true)
  recipe("genericStoreV4U32Sm50", ptx(genericVectorPtx), 60, "store_v4_u32", hex = true)
  recipe("hadd2F32Sm75", cuda(hadd2F32Cuda), 75, hex = true)
  recipe("hadd2SelectedHighSm89", cuda(hadd2SelectedCuda), 89, "hadd2_selected_high", hex = true)
  recipe("hadd2SelectedLowSm89", cuda(hadd2SelectedCuda), 89, "hadd2_selected_low", hex = true)
  recipe("hadd2Sm89", cuda(hadd2Cuda), 89)
  recipe("hfma2MmaAddReferenceSm89", cuda(hfma2MmaCuda), 89, "hfma2_add_reference", hex = true)
  recipe("hfma2SelectedSm89", cuda(hfma2SelectedCuda), 89)
  recipe("hfma2Sm89", cuda(hfma2Cuda), 89)
  for {
    a <- Vec("col", "row")
    b <- Vec("col", "row")
  } {
    val layout = a.capitalize + b.capitalize
    recipe(s"hmma884F16${layout}Sm70", ptx(hmma884F16Ptx(a, b)), 70, hex = true)
    recipe(s"hmma884F32${layout}Sm70", ptx(hmma884F32Ptx(a, b)), 70, hex = true)
  }
  recipe("hmma884F32ZeroSm70", ptx(hmma884F32ZeroPtx), 70, hex = true)
  recipe("hmmaF16AccAliasSm89", cuda(hmmaF16AccCuda), 89, "mma_f16_acc_alias")
  recipe("hmmaF16AccNonaliasSm89", cuda(hmmaF16AccCuda), 89, "mma_f16_acc_nonalias")
  recipe("hmma1688F16AccAliasSm89", cuda(hmmaF16AccCuda), 89, "mma_f16_acc_alias_1688")
  recipe("hmma1688F16AccNonaliasSm89", cuda(hmmaF16AccCuda), 89, "mma_f16_acc_nonalias_1688")
  recipe("hmma1688F16AccAliasSm75", ptx(hmmaF16AccSm75Ptx), 75, "mma_f16_acc_alias_1688")
  recipe("hmma1688F16AccNonaliasSm75", ptx(hmmaF16AccSm75Ptx), 75, "mma_f16_acc_nonalias_1688")
  recipe("hmma1688F16AccShuffleAliasSm75", cuda(hmmaF16AccShuffleCuda), 75, "mma_f16_acc_alias_1688")
  recipe("hmma1688F16AccShuffleNonaliasSm75", cuda(hmmaF16AccShuffleCuda), 75, "mma_f16_acc_nonalias_1688")
  generated("hmmaF16AccSm75Ptx", hmmaF16AccSm75Ptx, hmmaF16AccCuda, 75)
  recipe("hmnmx2MixedSm80", cuda(hmnmx2Cuda), 80, "hmnmx2_mixed_probe", hex = true)
  recipe("hmnmx2SelectSm80", cuda(hmnmx2Cuda), 80, "hmnmx2_select_probe", hex = true)
  recipe("hmul2SelectedSm89", cuda(hmul2SelectedCuda), 89)
  recipe("hmul2Sm89", cuda(hmul2Cuda), 89)
  recipe("hsetp2Sm89", cuda(hsetp2Cuda), 89, hex = true)
  recipe("hsetp2TwoSm70", ptx(hsetp2TwoPtx(70)), 70, hex = true)
  recipe("hsetp2TwoSm89", ptx(hsetp2TwoPtx(89)), 89, hex = true)
  recipe("iadd3AddConstantSubSm50", cuda(iadd3NegativeCuda), 50, "add_constant_sub")
  recipe("iadd3AddConstantSubSm60", cuda(iadd3NegativeCuda), 60, "add_constant_sub")
  recipe("iadd3AddLargeConstantSubSm50", cuda(iadd3NegativeCuda), 50, "add_large_constant_sub")
  recipe("iadd3AddLargeConstantSubSm60", cuda(iadd3NegativeCuda), 60, "add_large_constant_sub")
  recipe("iadd3AddSubSm50", cuda(iadd3NegativeCuda), 50, "add_sub")
  recipe("iadd3AddSubSm60", cuda(iadd3NegativeCuda), 60, "add_sub")
  recipe("iadd3CarrySm50", cuda(iadd3CarryCuda), 50, "probe")
  recipe("iadd3CarrySm60", cuda(iadd3CarryCuda), 60, "probe")
  recipe("iadd3ConstantSm50", cuda(iadd3ConstantCuda), 50, "probe_constant")
  recipe("iadd3ConstantSm60", cuda(iadd3ConstantCuda), 60, "probe_constant")
  recipe("iadd3MulwideSm50", ptx(iadd3MulwidePtx), 50)
  recipe("iadd3MulwideSm60", ptx(iadd3MulwidePtx), 60)
  recipe("iadd3MultipairSm50", cuda(iadd3MultipairCuda), 50, "probe_multi")
  recipe("iadd3MultipairSm60", cuda(iadd3MultipairCuda), 60, "probe_multi")
  recipe("iadd3NegativeConstantSubSm50", cuda(iadd3NegativeCuda), 50, "negative_constant_sub")
  recipe("iadd3NegativeConstantSubSm60", cuda(iadd3NegativeCuda), 60, "negative_constant_sub")
  recipe("i2fF16ImmediateSm80", cuda(i2fF16Cuda), 80, "i2f_f16_immediate_probe", hex = true)
  recipe("i2fF16Sm80", cuda(i2fF16Cuda), 80, "i2f_f16_probe", hex = true)
  recipe("i2fpSm86", ptx(i2fpPtx), 86)
  recipe("i2iCcSm35", ptx(i2iCcPtx), 35)
  recipe("i2iS16S8Sm35", ptx(i2iS16S8Ptx), 35)
  recipe("icmpSm35", ptx(icmpPtx), 35)
  for {
    sm     <- Vec(50, 60)
    kernel <- Vec("index_scaled", "index_scaled7", "index_scaled9")
  } {
    val name = kernel.split('_').map(_.capitalize).mkString
    recipe(s"iscadd${name}MinusBaseSm50", cuda(iscaddNegativeCuda), sm, s"${kernel}_minus_base")
  }
  recipe("isetEq64Sm35", cuda(isetEq64Cuda), 35, "eq64")
  recipe("isetEq64SignedSm35", cuda(isetEq64SignedCuda), 35, "eq64s")
  recipe("isetExtendedSm35", ptx(isetExtendedPtx), 35, hex = true)
  recipe("lds32Sm50", ptx(lds32Ptx), 50, hex = true)
  recipe("lds32Sm50", ptx(lds32Ptx), 60, hex = true)
  recipe("ldsmHmma1688TransSm80", cuda(ldsmHmma1688TransCuda), 80, hex = true)
  recipe("ldsmSm89", ptx(ldsmPtx), 89, hex = true)
  for {
    sm    <- Vec(50, 60)
    shift <- Vec("5", "6", "24")
  }
    recipe(s"leaDivS${shift}Sm$sm", if (sm == 50) ptx(leaDivPtx) else cuda(leaDivCuda), sm, s"div_s$shift", hex = true)
  generated("leaDivPtx", leaDivPtx, leaDivCuda, 50)
  for {
    sm    <- Vec(50, 60)
    shift <- Vec("0", "1", "2", "3", "31")
  }
    recipe(s"leaS${shift}Sm${if (shift == "0") 50 else sm}", ptx(leaPairsPtx(sm)), sm, s"lea_s$shift", hex = true)
  for (sm <- Vec(50, 60)) {
    recipe("logicSm50", ptx(logicPtx(sm)), sm, "logic_probe", hex = true)
    recipe("logicPredicateSm50", ptx(logicPtx(sm)), sm, "predicate_probe", hex = true)
  }
  for (sm <- Vec(75, 80, 86, 89)) recipe(s"lop3PredicateSm$sm", ptx(lop3PredicatePtx), sm, hex = true)
  recipe("membarSm35", ptx(membarPtx), 35, hex = true)
  recipe("minmaxSignedSm35", cuda(minmaxCuda), 35, "minmax_signed_probe")
  recipe("minmaxSm35", cuda(minmaxCuda), 35, "minmax_probe")
  recipe("multiplySubtractSm35", cuda(multiplySubtractCuda), 35)
  for (variant <- Vec("Dynamic", "Immediates", "Predicated"))
    recipe(s"nanosleep${variant}Sm75", cuda(nanosleepCuda), 75, s"nanosleep_${variant.toLowerCaseRoot}", hex = true)
  recipe("saxpyO0Sm89", ptx(saxpyO0Ptx), 89, hex = true, flags = "-O0")
  recipe("scalarSgemmSm89", ptx(scalarSgemmPtx), 89, hex = true)
  recipe("sgxtSm70", ptx(sgxtPtx), 70)
  recipe("sharedPointerSm35", ptx(sharedPointerSm35Ptx), 35)
  generated("sharedPointerSm35Ptx", sharedPointerSm35Ptx, sharedPointerCuda, 35)
  recipe("sharedPointerSm89", ptx(sharedPointerSm89Ptx), 89, hex = true, tools = Some(Cuda1178))
  recipe("stgU8UniformBaseSm89", ptx(stgU8UniformBasePtx(89)), 89)
  recipe("videoMinMaxSm35", ptx(videoMinMaxPtx), 35)
  for (sm <- Vec(50, 60)) {
    recipe("videoMinMaxNestedMaxSm50", cuda(videoMinMaxNestedCuda), sm, "nested_max")
    recipe("videoMinMaxNestedMinSm50", cuda(videoMinMaxNestedCuda), sm, "nested_min")
    recipe("xmadCompilerFormsSm50", ptx(xmadCompilerFormsPtx), sm, hex = true)
    recipe("xmadSignedHiSm50", ptx(xmadSignedHiPtx), sm, hex = true)
  }
  recipe("xmadArithmeticSm50", ptx(xmadArithmeticPtx), 50, hex = true)

  patched("fchkSm70Patched", 70, Vec("probe-sm70.ptx" -> fchkPtx(70)), "fchk-sm70.sass")(
    "ptxas -arch=sm_70 probe-sm70.ptx -o probe-sm70.cubin",
    """perl -0777 - probe-sm70.cubin > fchk-sm70.cubin <<'PERL'
    |$x=<>;
    |$o=pack("H*","0b7200020500000000d0f00300c84f00");
    |die "FSETP count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
    |$i=index($x,$o);
    |$p=unpack("Q<",substr($x,$i-8,8));
    |die "preceding control\n" unless substr($x,$i-8,8) eq pack("H*","ffe4ff0000e40f00");
    |substr($x,$i-8,8,pack("H*","ffe4ff0000e44f00"));
    |substr($x,$i,16,pack("H*","02730002050000000000000000620e00"));
    |$t=$i+16;
    |die "consumer opcode\n" unless substr($x,$t,8) eq pack("H*","077809ff01000000");
    |substr($x,$t+8,8,pack("H*","0000000000d02f00"));
    |print $x
    |PERL""".stripMargin,
    "nvdisasm -c -ndf -hex fchk-sm70.cubin > fchk-sm70.sass"
  )

  patched("fchkSm75Patched", 75, Vec("probe-sm75.ptx" -> fchkPtx(75)), "fchk-sm75.sass")(
    "ptxas -arch=sm_75 probe-sm75.ptx -o probe-sm75.cubin",
    """perl -0777 - probe-sm75.cubin > fchk-sm75.cubin <<'PERL'
    |$x=<>;
    |$o=pack("H*","0b7200000500000000d0f00300c84f00");
    |die "FSETP count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
    |$i=index($x,$o);
    |$p=unpack("Q<",substr($x,$i-8,8));
    |die "preceding control\n" unless substr($x,$i-8,8) eq pack("H*","000a000000e20f00");
    |substr($x,$i-8,8,pack("H*","000a000000e24f00"));
    |substr($x,$i,16,pack("H*","02730000050000000000000000620e00"));
    |$t=$i+16;
    |die "consumer opcode\n" unless substr($x,$t,8) eq pack("H*","077805ff01000000");
    |substr($x,$t+8,8,pack("H*","0000000000ca2f00"));
    |print $x
    |PERL""".stripMargin,
    "nvdisasm -c -ndf -hex fchk-sm75.cubin > fchk-sm75.sass"
  )

  for (sm <- Vec(80, 86, 89))
    patched(s"fchkSm${sm}Patched", sm, Vec("probe.ptx" -> fchkPtx(sm)), "fchk.sass", tools = Some(Cuda1378))(
      s"ptxas -arch=sm_$sm probe.ptx -o probe.cubin",
      """perl -0777 - probe.cubin > fchk.cubin <<'PERL'
      |$x=<>;
      |$o=pack("H*","0b72000205000000 00d0f00300c84f00"=~s/ //gr);
      |$n=pack("H*","0273000205000000 0000000000624e00"=~s/ //gr);
      |die "FSETP count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
      |$x =~ s/\Q$o\E/$n/;
      |$tail=pack("H*","077809ff010000000000000000ca0f00");
      |die "SEL tail count\n" unless index($x,$tail)>=0 && index($x,$tail)==rindex($x,$tail);
      |$i=index($x,$tail);
      |$chunk=substr($x,$i,0x30);
      |substr($x,$i+0x80,0x30,$chunk);
      |$nop=pack("H*","18790000000000000000000000c40f00");
      |substr($x,$i,0x80,$nop x 8);
      |substr($x,$i+0x88,8,pack("H*","0000000000ca2f00"));
      |$ex=pack("H*","041c040020010000");
      |die "exit metadata\n" unless index($x,$ex)>=0 && index($x,$ex)==rindex($x,$ex);
      |$q=index($x,$ex)+4;
      |substr($x,$q,4,pack("V",0x1a0));
      |print $x
      |PERL""".stripMargin,
      "nvdisasm -c fchk.cubin > fchk.sass"
    )

  patched("sharedUniformAddressCudaSm86Patched", 86, Vec("probe.cu" -> sharedUniformAddressCuda), "patched.sass")(
    "nvcc -ccbin g++-11 -arch=sm_86 -cubin -o original.cubin probe.cu",
    "nvdisasm -c -ndf -hex original.cubin > original.sass",
    "readelf -SW original.cubin | awk '$2 == \".text.probe\" { print \"0x\" $5; exit }' > section-offset.txt",
    "section_offset=$(cat section-offset.txt)",
    "cp original.cubin patched.cubin",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0x0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"00000a00ff017624000fe400078e00ff\"",
    "printf '%b' '\\0271\\0172\\0006\\0000\\0000\\0134\\0000\\0000\\0000\\0012\\0000\\0000\\0000\\0344\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0x0)) conv=notrunc status=none",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0xa0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"0000000002077fae000fe8000b921844\"",
    "printf '%b' '\\0256\\0177\\0007\\0002\\0000\\0000\\0000\\0000\\0106\\0010\\0222\\0013\\0000\\0350\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0xa0)) conv=notrunc status=none",
    "nvdisasm -c -ndf patched.cubin > patched.sass"
  )

  for (immediate <- Vec("38004000", "7c003c00", "7c01fc01", "7e013c00", "80000000", "fc007c00", "fe017e01"))
    patched(s"hfma2Constant${immediate.capitalize}Sm89Patched", 89, Vec("hfma2Mma.cu" -> hfma2MmaCuda), "output.sass")(
      "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2Mma.cu -o kernel.cubin",
      "perl -e '$x=do{local $/;<>}; $o=pack(\"H*\",\"31720902ff0000200500000000ca4f00\"); die \"baseline HFMA count\\n\" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o); $imm=hex(shift); $n=pack(\"Q<Q<\",(($imm<<32)|0xff097435),0x004fca00000001ff); $x =~ s/\\Q$o\\E/$n/; print $x' kernel.cubin 0x" +
        immediate + " > patched.cubin",
      """symbol=$(readelf -Ws patched.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
      |nvdisasm -c -ndf -hex -fun "$symbol" patched.cubin > output.sass""".stripMargin
    )

  patched(
    "hadd2F32ToHalfSm75Patched",
    75,
    Vec("probe.cu" -> hadd2F32ToHalfCuda),
    "probe-sm75-patched.sass",
    tools = Some(Cuda1178)
  )(
    "nvcc -ccbin /usr/bin/g++-11 -arch=sm_75 -cubin probe.cu -o probe-sm75.cubin",
    """perl -0777 - probe-sm75.cubin > probe-sm75-patched.cubin <<'PERL'
    |$x=<>;
    |sub once { my($x,$old,$new)=@_;
    |die "unexpected/missing bytes\n" unless index($$x,$old)>=0 && index($$x,$old)==rindex($$x,$old);
    |$$x =~ s/\Q$old\E/$new/;
    |} once(\$x,pack("H*","04730000000000000008200000244e00"),pack("H*","30720000ff0000a00004000000244e00"));
    |once(\$x,pack("H*","30720200ff0000a00008000000d01f00"),pack("H*","18790000000000000008000000d01f00"));
    |once(\$x,pack("H*","867900ff0200000004e9100c00e20f00"),pack("H*","867900ff0000000004e9100c00e20f00"));
    |print $x
    |PERL""".stripMargin,
    "nvdisasm -c -ndf -hex probe-sm75-patched.cubin > probe-sm75-patched.sass"
  )

  patched("hfma2MmaAliasSm89Patched", 89, Vec("hfma2-mma-sm89.cu" -> hfma2MmaCuda), "hfma2-mma-alias-sm89.sass")(
    "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-sm89.cu -o hfma2-mma-sm89.cubin",
    """perl -0777 - hfma2-mma-sm89.cubin > hfma2-mma-alias-sm89.cubin <<'PERL'
    |$x=<>;
    |$o=pack("H*","31720902ff0000200500000000ca4f00");
    |$n=pack("H*","35780502003c003c0500000000ca4f00");
    |$s=pack("H*","8679000609000000");
    |die "HFMA count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
    |$i=index($x,$o);
    |@s=();
    |$p=0;
    |while(($p=index($x,$s,$p))>=0){push @s,$p;
    |$p+=length($s)} die "store slots\n" unless @s==2 && $s[1]==$i+16;
    |substr($x,$i,length($o),$n);
    |substr($x,$s[1],8,pack("H*","8679000605000000"));
    |print $x
    |PERL""".stripMargin,
    """fun_index=$(readelf -Ws hfma2-mma-alias-sm89.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
    |nvdisasm -c -ndf -hex -fun "$fun_index" hfma2-mma-alias-sm89.cubin > hfma2-mma-alias-sm89.sass""".stripMargin
  )

  patched("hfma2MmaNonaliasSm89Patched", 89, Vec("hfma2-mma-sm89.cu" -> hfma2MmaCuda), "hfma2-mma-nonalias-sm89.sass")(
    "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-sm89.cu -o hfma2-mma-sm89.cubin",
    """perl -0777 - hfma2-mma-sm89.cubin > hfma2-mma-nonalias-sm89.cubin <<'PERL'
    |$x=<>;
    |$o=pack("H*","31720902ff0000200500000000ca4f00");
    |$n=pack("H*","35780902003c003c0500000000ca4f00");
    |die "HFMA count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
    |$x =~ s/\Q$o\E/$n/;
    |print $x
    |PERL""".stripMargin,
    """fun_index=$(readelf -Ws hfma2-mma-nonalias-sm89.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
    |nvdisasm -c -ndf -hex -fun "$fun_index" hfma2-mma-nonalias-sm89.cubin > hfma2-mma-nonalias-sm89.sass""".stripMargin
  )

  patched(
    "hfma2MmaZeroAliasSm89Patched",
    89,
    Vec("hfma2-mma-zero-sm89.cu" -> hfma2MmaCuda),
    "hfma2-mma-zero-alias-sm89.sass"
  )(
    "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hfma2-mma-zero-sm89.cu -o hfma2-mma-zero-sm89.cubin",
    """perl -0777 - hfma2-mma-zero-sm89.cubin > hfma2-mma-zero-alias-sm89.cubin <<'PERL'
    |$x=<>;
    |$o=pack("H*","31720902ff0000200500000000ca4f00");
    |$n=pack("H*","35720502ff0000200500000000ca4f00");
    |$s=pack("H*","8679000609000000");
    |die "HFMA count\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
    |$i=index($x,$o);
    |@s=();
    |$p=0;
    |while(($p=index($x,$s,$p))>=0){push @s,$p;
    |$p+=length($s)} die "store slots\n" unless @s==2 && $s[1]==$i+16;
    |substr($x,$i,length($o),$n);
    |substr($x,$s[1],8,pack("H*","8679000605000000"));
    |print $x
    |PERL""".stripMargin,
    """symbol=$(readelf -Ws hfma2-mma-zero-alias-sm89.cubin | awk '$NF=="hfma2_mma_probe"{gsub(":","",$1);print $1;exit}')
    |nvdisasm -c -ndf -hex -fun "$symbol" hfma2-mma-zero-alias-sm89.cubin > hfma2-mma-zero-alias-sm89.sass""".stripMargin
  )

  patched(
    "hmnmx2MixedAliasSm80Patched",
    80,
    Vec("hmnmx2-sm80.cu" -> hmnmx2Cuda),
    "hmnmx2-mixed-alias-patched-sm80.sass"
  )(
    "nvcc -ccbin g++-11 -arch=compute_80 -code=sm_80 -lineinfo -cubin hmnmx2-sm80.cu -o hmnmx2-sm80.cubin",
    """perl -0777 - hmnmx2-sm80.cubin > hmnmx2-mixed-alias-sm80.cubin <<'PERL'
    |$x=<>;
    |$a=pack("H*","4072070005000020");
    |$b=pack("H*","8679000207000000");
    |die "instruction/store count mismatch\n" unless index($x,$a)>=0 && index($x,$a)==rindex($x,$a) && index($x,$b)>=0 && index($x,$b)==rindex($x,$b);
    |$x =~ s/\Q$a\E/pack("H*","4072000005000020")/e;
    |$x =~ s/\Q$b\E/pack("H*","8679000200000000")/e;
    |print $x
    |PERL""".stripMargin,
    """fun_index=$(readelf -Ws hmnmx2-mixed-alias-sm80.cubin | awk '$NF=="hmnmx2_mixed_alias_probe"{gsub(":","",$1);print $1;exit}')
    |nvdisasm -c -ndf -hex -fun "$fun_index" hmnmx2-mixed-alias-sm80.cubin > hmnmx2-mixed-alias-patched-sm80.sass""".stripMargin
  )

  patched(
    "hmul2F32Sm75Patched",
    75,
    Vec("hmul2-f32.cu" -> hmul2F32Cuda),
    "hmul2-f32-sm75-patched.sass",
    tools = Some(Cuda1378)
  )(
    "nvcc -ccbin g++-11 -arch=sm_75 -cubin hmul2-f32.cu -o hmul2-f32-sm75.cubin",
    """perl -0777 - hmul2-f32-sm75.cubin > hmul2-f32-sm75-patched.cubin <<'PERL'
    |$x=<>;
    |@r=(["20720d00090000000000400000e40f04","32720d02040000200048000000cc0f00"],["20720f000b0000000000400000e40f00","32720f02040000300048000000cc0f00"],["20720908090000000000400000e40f04","3272090204000020004c000000cc0f00"],["20720b080b0000000000400000e40f00","32720b0204000030004c000000cc0f00"]);
    |for $r(@r){$o=pack("H*",$r->[0]);
    |$n=pack("H*",$r->[1]);
    |die "slot mismatch\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
    |$x =~ s/\Q$o\E/$n/;
    |} print $x
    |PERL""".stripMargin,
    "nvdisasm -c -hex hmul2-f32-sm75-patched.cubin > hmul2-f32-sm75-patched.sass"
  )

  patched("hsetp2GeuRawSm89Patched", 89, Vec("hsetp2-sm89.cu" -> hsetp2Cuda), "hsetp2-geu-raw-sm89.sass")(
    "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hsetp2-sm89.cu -o hsetp2-sm89.cubin",
    """perl -0777 - hsetp2-sm89.cubin > hsetp2-geu-raw-sm89.cubin <<'PERL'
    |$x=<>;
    |$o=pack("H*","347200020500002000e8f00300e48f00");
    |$n=pack("H*","347200020500000000e0f00300e48f00");
    |die "expected one opcode\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
    |$x =~ s/\Q$o\E/$n/;
    |print $x
    |PERL""".stripMargin,
    "nvdisasm -c -ndf -hex hsetp2-geu-raw-sm89.cubin > hsetp2-geu-raw-sm89.sass"
  )

  patched("hsetp2NeRawSm89Patched", 89, Vec("hsetp2-ne-sm89.cu" -> hsetp2NeCuda), "hsetp2-ne-raw-sm89.sass")(
    "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_89 hsetp2-ne-sm89.cu -o hsetp2-ne-sm89.cubin",
    """perl -0777 - hsetp2-ne-sm89.cubin > hsetp2-ne-raw-sm89.cubin <<'PERL'
    |$x=<>;
    |$o=pack("H*","34720002ff0000200058f00300c60f00");
    |$n=pack("H*","34720002ff0000000050f00300c60f00");
    |die "expected one opcode\n" unless index($x,$o)>=0 && index($x,$o)==rindex($x,$o);
    |$x =~ s/\Q$o\E/$n/;
    |print $x
    |PERL""".stripMargin,
    "nvdisasm -c -ndf -hex hsetp2-ne-raw-sm89.cubin > hsetp2-ne-raw-sm89.sass"
  )

  for (sm <- Vec(50, 60))
    patched("logicPredicateZeroSm50Patched", sm, Vec("probe.ptx" -> logicPtx(sm)), "zero.sass")(
      s"ptxas -arch=sm_$sm probe.ptx -o probe.cubin",
      "base=$(readelf -SW probe.cubin | awk '{for(i=1;i<=NF;i++) if($i==\".text.predicate_probe\") print \"0x\" $(i+3)}')",
      "printf '\\004\\000\\367\\001\\000\\040\\102\\070' | dd of=probe.cubin bs=1 seek=$((base + 0x50)) conv=notrunc status=none",
      "printf '\\005\\002\\102\\000\\000\\040\\334\\356' | dd of=probe.cubin bs=1 seek=$((base + 0x70)) conv=notrunc status=none",
      "printf '\\004\\002\\207\\000\\000\\040\\334\\356' | dd of=probe.cubin bs=1 seek=$((base + 0x78)) conv=notrunc status=none",
      "symbol=$(readelf -sW probe.cubin | awk '$NF == \"predicate_probe\" {sub(\":\", \"\", $1); print $1; exit}')",
      "nvdisasm -c -ndf -hex -fun \"$symbol\" probe.cubin > zero.sass"
    )

  patched("ldsmUniformSm89Patched", 89, Vec("probe.ptx" -> ldsmPtx), "uniform.sass")(
    "ptxas -arch=sm_89 probe.ptx -o probe.cubin",
    "nvdisasm -c -ndf -hex probe.cubin > probe.sass",
    """perl -0777 - probe.cubin > uniform.cubin <<'PERL'
    |$x=<>;
    |sub once { my($old,$new)=@_;
    |die "encoding mismatch\n" unless index($x,$old)>=0 && index($x,$old)==rindex($x,$old);
    |$x =~ s/\Q$old\E/$new/;
    |} once(pack("H*","247601ff000a0000ff008e0700e40f00"),pack("H*","82780600100000000000000000e40f00"));
    |once(pack("H*","3b781400000000000002000000280e00"),pack("H*","3b78140006f0ffff0002000800280e00"));
    |once(pack("H*","3b780600000000000001000000680e00"),pack("H*","3b78060006f0ffff0001000800680e00"));
    |once(pack("H*","3b780500000000000000000000a80e00"),pack("H*","3b78050006f0ffff0000000800a80e00"));
    |once(pack("H*","3b781800000000000042000000e80e00"),pack("H*","3b78180006f0ffff0042000800e80e00"));
    |print $x
    |PERL""".stripMargin,
    "nvdisasm -c -ndf -hex uniform.cubin > uniform.sass"
  )

  for (sm <- Vec(50, 60))
    patched(s"psetSm${sm}Patched", sm, Vec("probe.ptx" -> psetPtx), "pset.sass")(
      s"ptxas -arch=sm_$sm probe.ptx -o probe.cubin",
      "cp probe.cubin pset.cubin",
      "text_offset=$(readelf -SW probe.cubin | awk '{for(i=1;i<=NF;i++) if($i==\".text.probe\"){print \"0x\"$(i+3);exit}}'); test \"$(od -An -tx1 -N8 -j $((text_offset+0x38)) probe.cubin | tr -d ' \\n')\" = 00fff7ff7f04a039; perl -e 'print pack(\"H*\",shift)' 000007e080038850 | dd of=pset.cubin bs=1 seek=$((text_offset+0x38)) conv=notrunc status=none",
      "nvdisasm -c -ndf -hex pset.cubin > pset.sass"
    )

  patched("lop3PredicateSm70Patched", 70, Vec("original-sm70.ptx" -> lop3PredicateSm70Ptx), "probe-sm70.sass")(
    "ptxas -arch=sm_70 original-sm70.ptx -o original-sm70.cubin",
    """cp original-sm70.cubin probe-sm70.cubin
    |text_offset=$(readelf -SW original-sm70.cubin | awk '{for(i=1;i<=NF;i++) if($i==".text.probe"){print "0x"$(i+3);exit}}')
    |for spec in '0x90 0c720008ff0000007054740000e40f04 1272ff040500000006c0040000e40f04' '0xa0 0c720008ff0000007050720000e40f04 1272ff040500000006c0030000e40f04' '0xb0 0c720008ff0000007054700400e40f00 1272ff040500000006c0000400e40f00'; do
    |  set -- $spec; off=$1; old=$2; new=$3
    |  test "$(od -An -tx1 -N16 -j $((text_offset+off)) original-sm70.cubin | tr -d ' \n')" = "$old"
    |  perl -e 'print pack("H*",shift)' "$new" | dd of=probe-sm70.cubin bs=1 seek=$((text_offset+off)) conv=notrunc status=none
    |done""".stripMargin,
    "nvdisasm -c -ndf -hex probe-sm70.cubin > probe-sm70.sass"
  )

  patched("sgxtRawWidthSm70Patched", 70, Vec("probe-sm70.ptx" -> sgxtPtx), "probe-raw-width-sm70.sass")(
    "nvcc -ccbin g++-11 -cubin -arch=sm_70 probe-sm70.ptx -o probe-sm70.cubin",
    "text_offset=$(readelf -SW probe-sm70.cubin | awk '{for(i=1;i<=NF;i++) if($i==\".text.sgxt_probe\"){print \"0x\"$(i+3);exit}}'); perl -e '$b=hex(shift); local $/; $x=<>; for $p ([0x20,0xff<<16,12<<16],[0x50,0xff<<24,12<<24],[0x100,0xff<<32,12<<32],[0x110,0xff<<32,12<<32]) {$o=$b+$p->[0]; $w=unpack(\"Q<\",substr($x,$o,8)); substr($x,$o,8,pack(\"Q<\",($w & ~$p->[1]) | $p->[2]));} print $x' \"$text_offset\" probe-sm70.cubin > probe-raw-width-sm70.cubin",
    "nvdisasm -c -ndf -hex probe-raw-width-sm70.cubin > probe-raw-width-sm70.sass"
  )

  patched(
    "sharedUniformAddressSm75Patched",
    75,
    Vec("probe.ptx" -> sharedUniformAddressPtx(75, 100, "s8")),
    "patched.sass"
  )(
    "ptxas -arch=sm_75 probe.ptx -o original.cubin",
    "nvdisasm -c -ndf -hex original.cubin > original.sass",
    "readelf -SW original.cubin | awk '$2 == \".text.probe\" { print \"0x\" $5; exit }' > section-offset.txt",
    "section_offset=$(cat section-offset.txt)",
    "cp original.cubin patched.cubin",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0x0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"00000a00ff017624000fe400078e00ff\"",
    "printf '%b' '\\0271\\0172\\0006\\0000\\0000\\0132\\0000\\0000\\0000\\0012\\0000\\0000\\0000\\0344\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0x0)) conv=notrunc status=none",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0xa0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"0000800007057984000e280000005200\"",
    "printf '%b' '\\0204\\0171\\0005\\0007\\0006\\0200\\0000\\0000\\0000\\0122\\0000\\0010\\0000\\0050\\0016\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0xa0)) conv=notrunc status=none",
    "nvdisasm -c -ndf -hex patched.cubin > patched.sass"
  )

  patched(
    "sharedUniformAddressU8Sm75Patched",
    75,
    Vec("probe-u8.ptx" -> sharedUniformAddressPtx(75, 100, "u8")),
    "patched.sass"
  )(
    "ptxas -arch=sm_75 probe-u8.ptx -o original.cubin",
    "nvdisasm -c -ndf -hex original.cubin > original.sass",
    "readelf -SW original.cubin | awk '$2 == \".text.probe\" { print \"0x\" $5; exit }' > section-offset.txt",
    "section_offset=$(cat section-offset.txt)",
    "cp original.cubin patched.cubin",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0x0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"00000a00ff017624000fe400078e00ff\"",
    "printf '%b' '\\0271\\0172\\0006\\0000\\0000\\0132\\0000\\0000\\0000\\0012\\0000\\0000\\0000\\0344\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0x0)) conv=notrunc status=none",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0xa0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"0000800007057984000e280000005000\"",
    "printf '%b' '\\0204\\0171\\0005\\0007\\0006\\0200\\0000\\0000\\0000\\0120\\0000\\0010\\0000\\0050\\0016\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0xa0)) conv=notrunc status=none",
    "nvdisasm -c -ndf -hex patched.cubin > patched.sass"
  )

  patched(
    "sharedUniformAddressSm86Patched",
    86,
    Vec("probe.ptx" -> sharedUniformAddressPtx(86, 1000, "u32")),
    "patched.sass"
  )(
    "ptxas -arch=sm_86 probe.ptx -o original.cubin",
    "nvdisasm -c -ndf -hex original.cubin > original.sass",
    "readelf -SW original.cubin | awk '$2 == \".text.probe\" { print \"0x\" $5; exit }' > section-offset.txt",
    "section_offset=$(cat section-offset.txt)",
    "cp original.cubin patched.cubin",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0x0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"00000a00ff017624000fe400078e00ff\"",
    "printf '%b' '\\0271\\0172\\0006\\0000\\0000\\0132\\0000\\0000\\0000\\0012\\0000\\0000\\0000\\0344\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0x0)) conv=notrunc status=none",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0xb0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"0000800007057984000e280000004800\"",
    "printf '%b' '\\0204\\0171\\0005\\0007\\0006\\0200\\0000\\0000\\0000\\0110\\0000\\0010\\0000\\0050\\0016\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0xb0)) conv=notrunc status=none",
    "nvdisasm -c -ndf patched.cubin > patched.sass"
  )

  patched("sharedUniformCopySm86Patched", 86, Vec("probe.cu" -> sharedUniformCopyCuda), "patched.sass")(
    "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_86 probe.cu -o original.cubin",
    "nvdisasm -c -ndf -hex original.cubin > original.sass",
    "readelf -SW original.cubin | awk '$2 == \".text.probe\" { print \"0x\" $5; exit }' > section-offset.txt",
    "section_offset=$(cat section-offset.txt)",
    "cp original.cubin patched.cubin",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0x0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"00000a0000017a02000fe40000000f00\"",
    "printf '%b' '\\0271\\0172\\0006\\0000\\0000\\0134\\0000\\0000\\0000\\0012\\0000\\0000\\0000\\0344\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0x0)) conv=notrunc status=none",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0xf0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"0008000002077fae000fe8000b921844\"",
    "printf '%b' '\\0256\\0175\\0007\\0002\\0000\\0000\\0010\\0000\\0106\\0010\\0222\\0013\\0000\\0350\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0xf0)) conv=notrunc status=none",
    "nvdisasm -c -ndf patched.cubin > patched.sass"
  )

  patched("sharedUniformStoreSm86Patched", 86, Vec("probe.ptx" -> sharedUniformStorePtx), "patched.sass")(
    "ptxas -arch=sm_86 probe.ptx -o original.cubin",
    "nvdisasm -c -ndf -hex original.cubin > original.sass",
    "readelf -SW original.cubin | awk '$2 == \".text.probe\" { print \"0x\" $5; exit }' > section-offset.txt",
    "section_offset=$(cat section-offset.txt)",
    "cp original.cubin patched.cubin",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0x0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"00000a00ff017624000fe400078e00ff\"",
    "printf '%b' '\\0271\\0172\\0006\\0000\\0000\\0132\\0000\\0000\\0000\\0012\\0000\\0000\\0000\\0344\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0x0)) conv=notrunc status=none",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0xb0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"000080040b007388000fe80000000000\"",
    "printf '%b' '\\0210\\0171\\0000\\0011\\0004\\0200\\0000\\0000\\0006\\0000\\0000\\0010\\0000\\0350\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0xb0)) conv=notrunc status=none",
    "nvdisasm -c -ndf -hex patched.cubin > patched.sass"
  )

  patched("sharedUniformVectorStoreSm89Patched", 89, Vec("probe.ptx" -> sharedUniformVectorStorePtx), "patched.sass")(
    "ptxas -arch=sm_89 probe.ptx -o original.cubin",
    "nvdisasm -c -ndf -hex original.cubin > original.sass",
    "readelf -SW original.cubin | awk '$2 == \".text.probe\" { print \"0x\" $5; exit }' > section-offset.txt",
    "section_offset=$(cat section-offset.txt)",
    "cp original.cubin patched.cubin",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0x0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"00000a00ff017624000fe400078e00ff\"",
    "printf '%b' '\\0271\\0172\\0006\\0000\\0000\\0132\\0000\\0000\\0000\\0012\\0000\\0000\\0000\\0344\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0x0)) conv=notrunc status=none",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0x120)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"0002000814007388000fe80000000c00\"",
    "printf '%b' '\\0210\\0171\\0000\\0000\\0010\\0000\\0002\\0000\\0006\\0314\\0000\\0010\\0000\\0350\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0x120)) conv=notrunc status=none",
    "nvdisasm -c -ndf -hex patched.cubin > patched.sass"
  )

  patched("sharedUniformWordStoreSm89Patched", 89, Vec("probe.ptx" -> sharedUniformWordStorePtx), "patched.sass")(
    "ptxas -arch=sm_89 probe.ptx -o original.cubin",
    "nvdisasm -c -ndf -hex original.cubin > original.sass",
    "readelf -SW original.cubin | awk '$2 == \".text.probe\" { print \"0x\" $5; exit }' > section-offset.txt",
    "section_offset=$(cat section-offset.txt)",
    "cp original.cubin patched.cubin",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0x0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"00000a00ff017624000fe400078e00ff\"",
    "printf '%b' '\\0271\\0172\\0006\\0000\\0000\\0132\\0000\\0000\\0000\\0012\\0000\\0000\\0000\\0344\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0x0)) conv=notrunc status=none",
    "test \"$(dd if=original.cubin bs=1 skip=$((section_offset + 0xc0)) count=16 status=none | od -An -tx8 -N16 | tr -d ' \\n')\" = \"000080040b007388000fe80000000800\"",
    "printf '%b' '\\0210\\0171\\0000\\0011\\0004\\0200\\0000\\0000\\0006\\0110\\0000\\0010\\0000\\0350\\0017\\0000' | dd of=patched.cubin bs=1 seek=$((section_offset + 0xc0)) conv=notrunc status=none",
    "nvdisasm -c -ndf -hex patched.cubin > patched.sass"
  )

  patched("stgU8UniformBaseSm75Patched", 75, Vec("probe.ptx" -> stgU8UniformBasePtx(75)), "probe.sass")(
    "ptxas -arch=sm_75 probe.ptx -o probe.cubin",
    "section_offset=$(readelf -SW probe.cubin | awk '{for(i=1;i<=NF;i++) if($i==\".text.stg_u8_sys_u32_ur_probe\") print \"0x\" $(i+3)}')",
    "test \"$(od -An -tx8 -j $((section_offset+0x10)) -N16 probe.cubin | xargs)\" = '0000000000057919 000e240000002100'",
    "test \"$(od -An -tx8 -j $((section_offset+0x20)) -N16 probe.cubin | xargs)\" = '00005e0005007a0c 001fd80003f06070'",
    "test \"$(od -An -tx8 -j $((section_offset+0x40)) -N16 probe.cubin | xargs)\" = '00005c0005027a10 000fca0007f1e0ff'",
    "test \"$(od -An -tx8 -j $((section_offset+0x50)) -N16 probe.cubin | xargs)\" = '00005d00ff037624 000fd000000e06ff'",
    "test \"$(od -An -tx8 -j $((section_offset+0x60)) -N16 probe.cubin | xargs)\" = '0000000502007386 000fe20000116100'",
    "printf '\\031\\171\\000\\000\\000\\000\\000\\000\\000\\041\\000\\000\\000\\044\\016\\000' | dd of=probe.cubin bs=1 seek=$((section_offset+0x10)) conv=notrunc status=none",
    "printf '\\014\\172\\000\\000\\000\\136\\000\\000\\160\\140\\360\\003\\000\\330\\037\\000' | dd of=probe.cubin bs=1 seek=$((section_offset+0x20)) conv=notrunc status=none",
    "printf '\\002\\162\\003\\000\\000\\000\\000\\000\\000\\017\\000\\000\\000\\312\\017\\000' | dd of=probe.cubin bs=1 seek=$((section_offset+0x40)) conv=notrunc status=none",
    "printf '\\271\\172\\004\\000\\000\\134\\000\\000\\000\\012\\000\\000\\000\\320\\017\\000' | dd of=probe.cubin bs=1 seek=$((section_offset+0x50)) conv=notrunc status=none",
    "printf '\\206\\171\\000\\000\\003\\000\\000\\000\\004\\341\\020\\010\\000\\342\\017\\000' | dd of=probe.cubin bs=1 seek=$((section_offset+0x60)) conv=notrunc status=none",
    "nvdisasm -c -ndf -hex probe.cubin > probe.sass",
    "grep -F 'S2R R0, SR_TID.X' probe.sass",
    "grep -F 'ISETP.GE.U32.AND P0, PT, R0, c[0x0][0x178], PT' probe.sass",
    "grep -F 'MOV R3, R0' probe.sass",
    "grep -F 'ULDC.64 UR4, c[0x0][0x170]' probe.sass",
    "grep -F 'STG.E.U8.SYS [R0.U32+UR4], R3' probe.sass"
  )

  patched("tensorSm86", 86, Vec("tensor.cu" -> tensorCuda), "tensor-sm86.sass")(
    "printf '#include <mma.h>\\n#include <cuda_fp16.h>\\nnamespace cuda_tensor {\\n' > tensor-kernel.cu",
    "sed -n '/^__global__ void tensor_gemm(/,/^}/p' tensor.cu >> tensor-kernel.cu",
    "printf '}\\n' >> tensor-kernel.cu",
    "nvcc -ccbin /usr/bin/g++-11 -cubin -arch=sm_86 tensor-kernel.cu -o tensor-sm86.cubin",
    "nvdisasm -c -ndf tensor-sm86.cubin > tensor-sm86.sass"
  )

  for (sm <- Vec(35, 50, 60, 70, 75, 80, 86, 87, 89, 90, 100, 103, 120)) {
    val name   = s"saxpySm${if (sm == 89) 86 else if (sm > 100) 100 else sm}"
    val source = cuda(saxpyCuda)
    verifies(s"$name sm_$sm with code")(toolkit(sm), Vec(source)) {
      (directory, run) =>
        run(Vec(
          compile(source, sm, ""),
          s"nvdisasm -c -ndf${if (sm <= 70) " -hex" else ""} kernel.cubin > output.sass",
          "objcopy -I elf64-little --dump-section .text.saxpy=code.bin kernel.cubin /dev/null"
        ))
        assertEquals(Fixture.normalise(Files.readString(directory.resolve("output.sass"))), Fixture.listing(name))
        assertEquals(bytes.toHex(Files.readAllBytes(directory.resolve("code.bin"))), code(name))
    }
  }

  verifies("copyAddSm89 cubin")(
    (ptxas = "13.3.33", nvdisasm = None),
    Vec("copyAdd.ptx" -> copyAddPtx)
  ) {
    (directory, run) =>
      run(Vec("ptxas -O3 -arch=sm_89 copyAdd.ptx -o kernel.cubin"))
      assertEquals(
        bytes.toHex(Files.readAllBytes(directory.resolve("kernel.cubin"))),
        copyAddCubin
      )
  }

  verifies("saxpyO0Sm89 mutations")(
    (ptxas = "13.3.33", nvdisasm = Some("13.3.73")),
    Vec("saxpyO0.ptx" -> saxpyO0Ptx)
  ) { (directory, run) =>
    run(Vec("ptxas -arch=sm_89 -O0 saxpyO0.ptx -o saxpy.cubin"))
    val base = Files.readAllBytes(directory.resolve("saxpy.cubin"))
    FixtureMutations.saxpy.foreach { probe =>
      val id = probe.id
      require(id.nonEmpty && id.forall(c => c.isLetterOrDigit || c == '-'), s"Invalid probe name: $id")
      val _ = Files.write(directory.resolve(s"$id.cubin"), FixtureMutations(base, probe).fold(fail(_), identity))
      run(
        Vec(s"if nvdisasm -c -ndf -hex $id.cubin > $id.sass 2>&1; then echo 0 > $id.exit; else echo $$? > $id.exit; fi")
      )
      assertEquals(Files.readString(directory.resolve(s"$id.exit")).trim.toInt, probe.expectedExitCode, id)
      val listing = Files.readString(directory.resolve(s"$id.sass"))
      assert(
        listing.contains(probe.expectedOutput),
        s"$id: expected '${probe.expectedOutput}' in NVIDIA output:\n$listing"
      )
    }
  }
}
