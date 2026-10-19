package net.kurobako.gemstone.isa.amdgpu.asm

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.EncodingConstants.*
import net.kurobako.gemstone.string.*

private object LaneModifiers {
  private val Rows: Map[NonEmpty[String], Int] = Map(
    ("row_shl", DPP.ROW_SHL0),
    ("row_shr", DPP.ROW_SHR0),
    ("row_ror", DPP.ROW_ROR0),
    ("row_share", DPP.ROW_SHARE0),
    ("row_xmask", DPP.ROW_XMASK0)
  )
  private val Waves: Map[NonEmpty[String], Int] = Map(
    ("wave_shl", DPP.WAVE_SHL1),
    ("wave_rol", DPP.WAVE_ROL1),
    ("wave_shr", DPP.WAVE_SHR1),
    ("wave_ror", DPP.WAVE_ROR1)
  )
  private val Mirrors: Map[NonEmpty[String], Int] =
    Map(("row_mirror", DPP.ROW_MIRROR), ("row_half_mirror", DPP.ROW_HALF_MIRROR))
  private val Broadcast = Map(15 -> DPP.BCAST15, 31 -> DPP.BCAST31)
  private val Numeric   = Set("row_mask", "bank_mask", "bound_ctrl", "fi")

  private val SubDwordSelectors = Vector(EncodingField.DST_SEL, EncodingField.SRC0_SEL, EncodingField.SRC1_SEL)

  // row_newbcast reuses row_share's encoding, so the target decides the spelling.
  private def rows(isa: ISA): Map[NonEmpty[String], Int] =
    if (DppControl.rowNewBroadcast(isa)) Rows - "row_share" + (("row_newbcast", DPP.ROW_NEWBCAST_FIRST))
    else Rows

  def supports(format: EncodingFormat): Boolean       = format.isDpp || format.isSdwa
  def sdwa(layout: InstructionLayout): Boolean        = layout.format.isSdwa
  def dpp8(layout: InstructionLayout): Boolean        = layout.format.isDpp8
  def base(layout: InstructionLayout): EncodingFormat = layout.format.base

  def selected(instruction: Instruction, layout: InstructionLayout): Boolean = {
    val eight   = instruction.mods.exists(_.name == "dpp8")
    val subword = instruction.mods.exists(mod =>
      PartialFunction.cond(mod.name) { case "sdwa" | "src0_sel" | "src1_sel" | "dst_sel" | "dst_unused" => true }
    )
    val dpp = instruction.mods.exists(mod =>
      mod.name == "dpp" || mod.name == "dpp8" || mod.name == "quad_perm" || Rows.contains(mod.name) ||
        Waves.contains(mod.name) || Mirrors.contains(mod.name) || mod.name == "row_bcast" || mod.name == "row_newbcast"
    )
    if (subword) sdwa(layout)
    else if (dpp) supports(layout.format) && !sdwa(layout) && dpp8(layout) == eight
    else !supports(layout.format)
  }

  def defaults(layout: InstructionLayout): Map[EncodingField, BigInt] =
    if (!supports(layout.format)) Map.empty
    else if (sdwa(layout)) Map(EncodingField.SRC0 -> BigInt(Fixed.SdwaSource)) ++
      SubDwordSelectors.filter(name =>
        layout.byName.contains(name) && (name != EncodingField.SRC1_SEL || base(layout) != EncodingFormat.VOP1)
      ).map(_ -> BigInt(SubDwordSelection.DWord.encoding)).toMap
    else if (dpp8(layout)) Map(EncodingField.SRC0 -> BigInt(DPP8.DPP8_FI_0))
    else Map(
      EncodingField.SRC0      -> BigInt(Fixed.DppSource),
      EncodingField.ROW_MASK  -> BigInt(15),
      EncodingField.BANK_MASK -> BigInt(15)
    )

  private def number(value: String): Option[Int]                               = parseIntLiteral(value)
  private def list(value: String, count: Int, limit: Int): Option[Vector[Int]] =
    Option.when(value.startsWith("[") && value.endsWith("]"))(value.drop(1).dropRight(1).split(',').toVector)
      .flatMap(_.traverse(text => number(text.trim))).filter(values =>
        values.size == count && values.forall(value => value >= 0 && value < limit)
      )

  def modifier(modifier: Modifier, layout: InstructionLayout): Option[Map[EncodingField, BigInt]] = {
    val name               = modifier.name
    val value              = modifier.value.map(_.raw)
    def control(code: Int) = Option.when(
      layout.byName.contains(EncodingField.DPP_CTRL) && DppControl.valid(code, layout.isa)
    )(Map(EncodingField.DPP_CTRL -> BigInt(code)))
    if (name == "dpp" || name == "sdwa")
      Option.when(value.isEmpty && supports(layout.format) && (name == "sdwa") == sdwa(layout))(Map.empty)
    else if (name == "dpp8") value.flatMap(list(_, count = 8, limit = 8)).filter(_ => dpp8(layout)).map(values =>
      values.zipWithIndex.map { case (lane, index) => EncodingField.LaneSelectors(index) -> BigInt(lane) }.toMap
    )
    else if (name == "quad_perm") value.flatMap(list(_, count = 4, limit = 4)).flatMap(values =>
      control(values.zipWithIndex.map { case (lane, index) => lane << (index * 2) }.sum)
    )
    else if (rows(layout.isa).contains(name)) value.flatMap(number(_)).filter(value => value >= 0 && value < 16)
      .flatMap(value => control(rows(layout.isa)(name) | value))
    else if (Waves.contains(name)) value.flatMap(number(_)).filter(_ == 1).flatMap(_ => control(Waves(name)))
    else if (Mirrors.contains(name))
      Option.when(value.isEmpty || value.contains("1"))(Mirrors(name)).flatMap(control(_))
    else if (name == "row_bcast") value.flatMap(number(_)).flatMap(Broadcast.get).flatMap(control(_))
    else if (Numeric(name)) value.flatMap(number(_)).flatMap { value =>
      if (name == "fi" && dpp8(layout))
        Option.when(value == 0 || value == 1)(Map(EncodingField.SRC0 -> BigInt(DPP8.DPP8_FI_0 + value)))
      else EncodingField.fromName(name.toUpperCaseRoot).filter(layout.byName.contains).map(field =>
        Map(field -> BigInt(value))
      )
    }
    else if (name == "dst_unused") value.flatMap(SubDwordDestinationUnused.fromName)
      .filter(_ => layout.byName.contains(EncodingField.DST_UNUSED))
      .map(selection => Map(EncodingField.DST_UNUSED -> BigInt(selection.encoding)))
    else if (Set("dst_sel", "src0_sel", "src1_sel")(name)) for {
      text      <- value
      selection <- SubDwordSelection.fromName(text)
      field     <- EncodingField.fromName(name.toUpperCaseRoot).filter(layout.byName.contains)
    } yield Map(field -> BigInt(selection.encoding))
    else None
  }

  def modifiers(word: InstructionWord, layout: InstructionLayout): Vec[Modifier] = {
    def numeric(name: NonEmpty[String], value: Long): Modifier =
      Modifier(name, Some(value.toString.unsafe[NonEmpty[String]]))
    inline def select(name: EncodingField, inline spelling: Int => Option[NonEmpty[String]]): Vec[Modifier] =
      layout.byName.get(name).flatMap(field =>
        spelling(field.get(word).toInt)
      )
        .map(value =>
          Modifier(name.assemblyName, Some(value))
        ).toVec
    if (!supports(layout.format)) Vec.empty
    else if (sdwa(layout)) Vec(Modifier("sdwa")) ++
      select(EncodingField.DST_SEL, SubDwordSelection.fromEncoding(_).map(_.assemblyName)) ++
      select(EncodingField.DST_UNUSED, SubDwordDestinationUnused.fromEncoding(_).map(_.assemblyName)) ++
      select(EncodingField.SRC0_SEL, SubDwordSelection.fromEncoding(_).map(_.assemblyName)) ++
      (if (base(layout) == EncodingFormat.VOP1) Vec.empty[Modifier]
       else select(EncodingField.SRC1_SEL, SubDwordSelection.fromEncoding(_).map(_.assemblyName)))
    else if (dpp8(layout)) Vec(
      Modifier("dpp"),
      Modifier(
        "dpp8",
        Some((0 until 8).map(index => layout.get(word, EncodingField.LaneSelectors(index))).mkString(
          "[",
          ",",
          "]"
        ).unsafe[NonEmpty[String]])
      )
    ) ++
      Option.when(layout.get(word, EncodingField.SRC0) == DPP8.DPP8_FI_1)(Modifier("fi", Some("1"))).toVec
    else {
      val code    = layout.get(word, EncodingField.DPP_CTRL).toInt
      val control = if (code < 256) Some(Modifier(
        "quad_perm",
        Some((0 until 4).map(index => (code >> (2 * index)) & 3).mkString("[", ",", "]").unsafe[NonEmpty[String]])
      ))
      else rows(layout.isa).find { case (_, base) => (code & ~15) == base }
        .map { case (name, _) => numeric(name, code & 15) }
        .orElse(Waves.find(_._2 == code).map { case (name, _) => numeric(name, 1) })
        .orElse(Mirrors.find(_._2 == code).map { case (name, _) => Modifier(name) })
        .orElse(Broadcast.find(_._2 == code).map { case (value, _) => numeric("row_bcast", value) })
      Vec(Modifier("dpp")) ++ control.toVec ++
        Vec(
          numeric("row_mask", layout.get(word, EncodingField.ROW_MASK)),
          numeric("bank_mask", layout.get(word, EncodingField.BANK_MASK))
        ) ++
        Vec(EncodingField.BOUND_CTRL, EncodingField.FI).filter(name => layout.get(word, name) != 0).map(name =>
          numeric(name.assemblyName, layout.get(word, name))
        )
    }
  }
}
