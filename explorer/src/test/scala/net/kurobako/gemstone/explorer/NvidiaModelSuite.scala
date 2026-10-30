package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Suite
import scala.concurrent.duration.*

class NvidiaModelSuite extends Suite {
  // the per-target sweeps lower every form of a catalogue
  override def baseTimeout: FiniteDuration = 5.minutes

  private def entry(model: Model, target: Option[String], mnemonic: String): View.Entry =
    model.entries(target).find(_.mnemonic == mnemonic).getOrElse(fail(s"missing $mnemonic"))

  private def form(model: Model, target: Option[String], mnemonic: String, name: String): (View.Entry, View.Form) = {
    val found = entry(model, target, mnemonic)
    (found, found.forms.find(_.name == name).getOrElse(fail(s"missing $name")))
  }

  private def instances(model: Model, target: Option[String], mnemonic: String): Vec[String] = {
    val found = entry(model, target, mnemonic)
    found.forms.flatMap(model.describe(found, _)).map(_.instance)
  }

  test("PTX instances cover every value of each required modifier group") {
    val setp = instances(PTXModel, None, "setp")
    Vec("eq", "ne", "lt", "le", "gt", "ge", "equ", "neu", "nan", "num").foreach(comparison =>
      assert(setp.exists(_.startsWith(s"setp.$comparison.")), comparison)
    )
    assert(instances(PTXModel, None, "atom").exists(_.startsWith("atom.inc.u32 ")))
  }

  test("PTX instances spell addresses, vectors and tensor fragments") {
    val ld = instances(PTXModel, None, "ld")
    assert(ld.contains("ld.u32 operand0, [operand1];"), ld)
    val vectors = PTXModel.instances("ld", "Ld 2").map(_.text)
    assert(vectors.contains("ld.v2.b128 {operand0_0, operand0_1}, [operand1];"), vectors)
    val mma = instances(PTXModel, None, "mma")
    assert(mma.exists(_.contains("{operand0_0, operand0_1, operand0_2, operand0_3}, {operand1_0, operand1_1}")), mma)
  }

  test("PTX matrix products of every element kind define one element of D, and data movement lowers to Tensor") {
    def described(mnemonic: String) = {
      val found = entry(PTXModel, None, mnemonic)
      found.forms.flatMap(PTXModel.describe(found, _))
    }
    val products = described("mma") ++ described("mma.sp") ++ described("mma.sp::ordered_metadata")
    Vec(".s4.", ".b1.", ".e4m3.", "kind::f8f6f4", "kind::mxf4nvf4", "mma.sp.", "mma.sp::ordered_metadata").foreach(
      kind =>
        assert(products.exists(_.instance.contains(kind)), kind)
    )
    assert(products.forall(_.definition.results.map(_.name) == Vec("d")))
    val scaled = products.find(_.instance.contains("block_scale")).get.definition
    assert(
      scaled.parameters.exists(_.name == "sa0") && scaled.parameters.exists(_.name == "sb0"),
      ScalaBackend.show(scaled)
    )
    val sparse = products.find(_.instance.startsWith("mma.sp.")).get.definition
    assert(sparse.parameters.exists(_.name == "e0"), ScalaBackend.show(sparse))
    def reason(mnemonic: String) = {
      val found = entry(PTXModel, None, mnemonic)
      PTXModel.unavailable(found, found.forms.head)
    }
    Vec("ldmatrix", "stmatrix", "movmatrix", "wmma.load").foreach(mnemonic =>
      assertEquals(reason(mnemonic), Unavailable.Mechanics("Lowers to Tensor."), mnemonic)
    )
  }

  test("PTX warpgroup products define one element of D from A at a descriptor or in registers, dense and sparse") {
    def described(mnemonic: String) = {
      val found = entry(PTXModel, None, mnemonic)
      found.forms.flatMap(PTXModel.describe(found, _))
    }
    val products = described("wgmma.mma_async") ++ described("wgmma.mma_async.sp")
    Vec(
      ".f16.f16.f16 ",
      ".bf16.",
      ".tf32.",
      ".e5m2.",
      ".s8.u8",
      ".b1.",
      "{a_0, a_1, a_2, a_3}",
      "aDesc",
      ".sp."
    ).foreach(kind => assert(products.exists(_.instance.contains(kind)), kind))
    assert(products.forall(_.definition.results.map(_.name) == Vec("d")))
    val scaled = products.find(_.instance.contains("scaleD")).get.definition
    assert(
      scaled.parameters.exists(_.name == "scaleD") && ScalaBackend.show(scaled).contains("ProductArithmetic.negate"),
      ScalaBackend.show(scaled)
    )
    val dropped = products.find(product => !product.instance.contains("scaleD")).get.definition
    assert(dropped.parameters.exists(_.name == "c"), ScalaBackend.show(dropped))
    def reason(mnemonic: String) = {
      val found = entry(PTXModel, None, mnemonic)
      PTXModel.unavailable(found, found.forms.head)
    }
    Vec("wgmma.fence", "wgmma.commit_group", "wgmma.wait_group").foreach(mnemonic =>
      assertEquals(
        reason(mnemonic),
        Unavailable.Mechanics(
          "It orders asynchronous warpgroup matrix products, which the VM completes when issued."
        ),
        mnemonic
      )
    )
  }

  test("SASS warpgroup products define one element of D, and B from registers is never emitted") {
    Vec("HGMMA", "IGMMA", "QGMMA", "BGMMA").foreach { mnemonic =>
      val found = entry(SASSModel, Some("sm_90"), mnemonic)
      found.forms.foreach { selected =>
        if (selected.name.contains("_URa_Rb_Rc_"))
          assert(SASSModel.unavailable(found, selected) match {
            case Unavailable.NotModelled(detail) => detail.contains("B from registers")
            case _                               => false
          })
        else assert(SASSModel.describe(found, selected).nonEmpty, selected.name)
      }
    }
    val (warpgroup, arrive) = form(SASSModel, Some("sm_90"), "WARPGROUP", "warpgroup_arrive_")
    assertEquals(
      SASSModel.unavailable(warpgroup, arrive),
      Unavailable.Mechanics(
        "It orders asynchronous warpgroup matrix products, which the VM completes when issued."
      )
    )
  }

  test("SASS matrix products define one element of D on each architecture that has them") {
    Vec(
      "sm_75"  -> Vec("HMMA", "IMMA", "BMMA"),
      "sm_89"  -> Vec("HMMA", "IMMA", "BMMA", "DMMA", "QMMA"),
      "sm_90"  -> Vec("HMMA", "IMMA", "BMMA", "DMMA"),
      "sm_120" -> Vec("HMMA", "IMMA", "DMMA", "QMMA", "OMMA")
    ).foreach { (target, mnemonics) =>
      mnemonics.foreach { mnemonic =>
        val found = entry(SASSModel, Some(target), mnemonic)
        assert(found.forms.exists(SASSModel.describe(found, _).nonEmpty), s"$target $mnemonic")
      }
    }
  }

  test("PTX forms without a definition say why, from how the VM treats their instances") {
    def reason(mnemonic: String, name: String) = {
      val (found, selected) = form(PTXModel, None, mnemonic, name)
      assert(PTXModel.describe(found, selected).isEmpty, name)
      PTXModel.unavailable(found, selected)
    }
    assertEquals(reason("bar", "Bar 2"), Unavailable.Mechanics("Lowers to BarrierSync."))
    assert(reason("nanosleep", "NanoSleep 1") match {
      case Unavailable.NotModelled(_) => true
      case _                          => false
    })
    assert(reason("prefetch", "Prefetch 1") match {
      case Unavailable.NotModelled(_) => true
      case _                          => false
    })
    assert(reason("cp.async.wait_group", "CpAsyncWaitGroup 1") match {
      case Unavailable.Mechanics(_) => true
      case _                        => false
    })
    assertEquals(reason("call", "Call 3"), Unavailable.Mechanics("Lowers to Call."))
    assert(
      reason("fabric.submit", "FabricSubmit 1") match {
        case Unavailable.NotModelled(detail) => detail.contains("Fabric")
        case _                               => false
      }
    )
    assert(reason("txq", "Txq 1") match {
      case Unavailable.NotModelled(_) => true
      case _                          => false
    })
  }

  test("SASS instances spell constant banks, uniform constant banks, indexed constants and addresses") {
    val dadd = instances(SASSModel, Some("sm_89"), "DADD")
    assert(dadd.contains("DADD R0, R4, c[0x3][0x10]"), dadd)
    val (_, cx) = form(SASSModel, Some("sm_90"), "DADD", "dadd__RRCx_RCx")
    val texts   = SASSModel.instances(Some("sm_90"), "DADD", cx.name).getOrElse(fail("no instance")).map(_.text)
    assert(texts.contains("DADD R0, R4, cx[UR8][0x10]"), texts)
    assert(instances(SASSModel, Some("sm_90"), "LDC").contains("LDC R0, c[0x3][R4]"))
    assert(instances(SASSModel, Some("sm_90"), "LDG").contains("LDG R0, [R4]"))
  }

  test("SASS instances cover every value of each modifier field") {
    val fsetp = instances(SASSModel, Some("sm_90"), "FSETP")
    Vec("LT", "EQ", "LE", "GT", "NE", "GE", "NUM", "NAN", "LTU", "EQU", "LEU", "GTU", "NEU", "GEU").foreach(
      comparison => assert(fsetp.exists(_.startsWith(s"FSETP.$comparison.")), comparison)
    )
  }

  test("SASS operand slots and encoded fields show the domain of the form") {
    val (_, bsync) = form(SASSModel, Some("sm_90"), "BSYNC", "bsync_")
    assertEquals(bsync.operands.find(_.name == "barReg").map(_.detail), Some("B0..B15"))
    val encoded = bsync.encoding.getOrElse(fail("no encoding")).fields.find(_.name == "barReg").map(_.detail)
    assertEquals(encoded, Some("BITS_4_19_16_barReg = barReg; B0..B15"))
    val (_, s2r) = form(SASSModel, Some("sm_90"), "S2R", "s2r_")
    assertEquals(
      s2r.operands.find(_.name == "Rd").map(_.detail),
      Some("R0..R253, RZ")
    )
  }

  test("PTX operand slots show the register class by type and the immediates the document limits") {
    val bfe = entry(PTXModel, None, "bfe").forms.head
    assertEquals(bfe.syntax.collect { case View.Token.Slot(_, detail) => detail }.head, "modifier .type")
    assertEquals(bfe.choices.headOption.map(_.name), Some(".type"))
    val destination = bfe.operands.head.domain.getOrElse(fail("no domain for the destination"))
    assert(destination.values.exists((name, detail) => name == ".u32" && detail == "32-bit unsigned integer register"))
    val length = bfe.operands(3).domain.getOrElse(fail("no domain for the length"))
    assertEquals(length.summary, ".u32 register, immediate 0..255 (integer-arithmetic-instructions-bfe)")
    val shift = entry(PTXModel, None, "shl").forms.head.operands(2).domain.getOrElse(fail("no domain for the amount"))
    assert(shift.summary.startsWith(".u32 register"), shift.summary)
    val load = entry(PTXModel, None, "ld").forms.head.operands.find(_.kind == "address").flatMap(_.domain)
    assertEquals(load.map(_.summary), Some("address: [reg], [reg+imm], [imm]"))
    val barrier = entry(PTXModel, None, "bar").forms.flatMap(_.operands).flatMap(_.domain)
    assert(barrier.exists(_.values.exists((kind, limit) => kind.startsWith("immediate") && limit.startsWith("0..15"))))
  }

  test("SASS forms without a definition say why, from how the VM treats their instances") {
    val (bar, sync) = form(SASSModel, Some("sm_90"), "BAR", "bar__SYNC_II_optionalCount_II")
    assertEquals(SASSModel.unavailable(bar, sync), Unavailable.Mechanics("Lowers to BarrierSync."))
    val (dadd, cx) = form(SASSModel, Some("sm_90"), "DADD", "dadd__RRCx_RCx")
    assert(SASSModel.unavailable(dadd, cx) match {
      case Unavailable.NotModelled(detail) => detail.contains("bindless constant buffer")
      case _                               => false
    })
    val (ttu, load) = form(SASSModel, Some("sm_75"), "__HIR0X005", "ttuld_")
    assert(SASSModel.unavailable(ttu, load) match {
      case Unavailable.NotModelled(detail) => detail.contains("tree traversal unit")
      case _                               => false
    })
    val (registers, release) = form(SASSModel, Some("sm_90"), "USETMAXREG", "usetmaxregAlloc__Ib_dealloc")
    assertEquals(SASSModel.unavailable(registers, release), Unavailable.Mechanics("Lowers to NoOp."))
  }

  test("SASS instances take the immediates, register pairs and optional modifiers their encodings require") {
    // ptxas 13.3 lowers mma.sp::ordered_metadata.m16n8k64.f32.e4m3.e4m3.f32 to
    // QMMA.SP.16864.F32.E4M3.E4M3 R4, R4.ROW, R8.COL, R12, R0, 0x0 on sm_89 and sm_120
    Vec("sm_89", "sm_120").foreach { target =>
      val (qmma, sparse) = form(SASSModel, Some(target), "QMMA", "qmma_sp_")
      assert(
        SASSModel.describe(qmma, sparse).exists(described =>
          described.instance.startsWith("QMMA.SP.16864.") && described.instance.endsWith(", 0x0")
        ),
        target
      )
    }
    assert(instances(SASSModel, Some("sm_35"), "SHFL").contains("SHFL.IDX P0, R0, R4, R8, R9"))
    assert(instances(SASSModel, Some("sm_52"), "LEA").contains("LEA.HI R0, R4, R8, R12, 0x1"))
    val (tensor, multicast) = form(SASSModel, Some("sm_90"), "UTMALDG", "utmaldg_URc__UUU")
    val loads               =
      SASSModel.instances(tensor.target, tensor.mnemonic, multicast.name).getOrElse(fail("no instance"))
    assert(loads.forall(load => load.text.contains(".MULTICAST") || load.text.contains(".IM2COL")), loads)
    val (work, next) = form(SASSModel, Some("sm_100"), "UGETNEXTWORKID", "ugetnextworkid_")
    assert(SASSModel.instances(work.target, work.mnemonic, next.name).isRight)
    assert(SASSModel.unavailable(work, next) match {
      case Unavailable.Mechanics(detail) => detail.contains("cluster launch control")
      case _                             => false
    })
  }

  test("2CTA tcgen05 products take a disabled-lane mask aligned to its eight registers") {
    val products = SASSModel.entries(Some("sm_100")).filter(_.mnemonic.startsWith("UTC"))
      .flatMap(entry => entry.forms.collect { case form if form.name.contains("_2cta") => entry -> form })
    assert(products.size >= 16, products.map(_._2.name))
    products.foreach((entry, product) =>
      assert(SASSModel.instances(entry.target, entry.mnemonic, product.name).isRight, product.name)
    )
    val (half, pair) = form(SASSModel, Some("sm_100"), "UTCHMMA", "utchmma_2cta__A_gdesc")
    assert(
      SASSModel.instances(half.target, half.mnemonic, pair.name).exists(_.forall(_.text.contains(", UR24, "))),
      pair.name
    )
  }

  test("tcgen05 products, tensor memory claims, release stores and shared locks have machine semantics") {
    val mnemonics = Set("UTCHMMA", "UTCIMMA", "UTCQMMA", "UTCOMMA", "UTCATOMSWS", "USTGR", "UREDGR", "LDSLK", "STSCUL")
    val checked   = Vec("sm_35", "sm_37", "sm_100", "sm_101", "sm_103", "sm_110").flatMap { target =>
      SASSModel.entries(Some(target)).flatMap { entry =>
        def machine(form: View.Form): Vec[Int] = SASSModel.semanticsOf(entry, form) match {
          case Semantics.Machine(_) | Semantics.Both(_, _) => Vec(1)
          case other                                       => fail(s"$target ${entry.mnemonic} ${form.name}: $other")
        }
        if (mnemonics(entry.mnemonic)) entry.forms.flatMap(form =>
          if (form.name.startsWith("utcatomsws_cas_")) Vec.empty[Int] else machine(form)
        )
        else Vec.empty[Int]
      }
    }
    assert(checked.size >= 100, checked.size)
  }

  test("PTX tcgen05.mma products have machine semantics") {
    Vec("tcgen05.mma", "tcgen05.mma.sp", "tcgen05.mma.ws", "tcgen05.mma.ws.sp").foreach { mnemonic =>
      val product = entry(PTXModel, None, mnemonic)
      product.forms.foreach(form =>
        PTXModel.semanticsOf(product, form) match {
          case Semantics.Machine(_) | Semantics.Both(_, _) => ()
          case other                                       => fail(s"$mnemonic ${form.name}: $other")
        }
      )
    }
  }

  test("SASS forms whose every instance the encoding tables reject are not modelled") {
    Vec(
      ("sm_52", "ATOM", "ATOM_CAS_RZ_and_Rc", "Register Rb cannot be RZ for ATOM.CAS"),
      ("sm_52", "ATOMS", "ATOMS_CAST_RZ_and_Rc", "Register Rb cannot be RZ for ATOMS.CAS/ATOMS.CAST"),
      ("sm_52", "F2F", "F2F_2_64_16", "F16 cannot be converted to F64"),
      ("sm_52", "VSHL", "SImm_VSHL", "No signed integer format allowed for shift count"),
      ("sm_70", "HMMA", "hmma__444_f32", "HMMA.444 is unsupported")
    ).foreach { (target, mnemonic, name, condition) =>
      val (found, selected) = form(SASSModel, Some(target), mnemonic, name)
      assertEquals(
        SASSModel.unavailable(found, selected),
        Unavailable.NotModelled(s"The encoding tables reject every instance the form can take: $condition.")
      )
    }
  }

  for ((language, target) <- Vec(Language.PTX -> None, Language.SASS -> Some("sm_90")))
    test(s"every $language form ${target.fold("")(name => s"on $name ")}is described or says why not") {
      val model = language.model
      model.entries(target).foreach(found =>
        found.forms.foreach(selected =>
          if (model.describe(found, selected).isEmpty)
            assertNotEquals(model.unavailable(found, selected), Unavailable.Unclassified, selected.name)
        )
      )
    }
}
