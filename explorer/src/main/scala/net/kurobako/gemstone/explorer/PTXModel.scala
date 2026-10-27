package net.kurobako.gemstone.explorer

import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.isa.ptx.asm.*
import net.kurobako.gemstone.semantics.render.*
import scala.collection.mutable

/** PTX instruction definitions and the specification metadata of each; PTX is a text ISA without an encoding. */
object PTXModel extends Model {

  /** The ordering instructions the VM runs with no effect, and why, keyed by the mnemonics they match. */
  private val orderingOnly: OrderingOnly = OrderingOnly(
    (
      "cp\\.async(\\.bulk)?\\.(commit_group|wait_group)|cp\\.async\\.wait_all",
      ".*",
      "asynchronous copies complete when issued, so no group is pending."
    ),
    ("setmaxnreg", ".*", "the VM does not limit the registers of a thread."),
    (
      "tcgen05\\.wait::(ld|st)",
      ".*",
      "tensor memory moves complete when issued, so there is nothing to wait for."
    ),
    (
      "wgmma\\.(fence|commit_group|wait_group)",
      ".*",
      "warpgroup matrix products complete when issued, so no group is pending."
    )
  )
  private def minimumTargets(definition: Instruction.Definition): Vec[Target] =
    definition.meta.entries.raw.map(_.minimumTarget)

  lazy val targets: Vec[String] =
    Instruction.definitions.flatMap(minimumTargets(_)).map(_.raw).distinct.sorted.map(sm => s"sm_$sm")
  val anyTarget: Boolean            = true
  def defaultTarget: Option[String] = None

  // Definitions name their operands only in the generated instruction cases.
  @SuppressWarnings(Array("scalafix:BannedSyntax.product-traversal"))
  private def operandNames(definition: Instruction.Definition, count: Int): Vec[String] = {
    val placeholder = Operand.Symbol("_".unsafe[NonEmpty[String]])
    definition.decoder(Vec.fill(count)(placeholder), Vec.empty).toOption
      .map(_.productElementNames.toVec)
      .filter(_.size == count)
      .getOrElse(Vec.tabulate(count)(index => s"operand$index"))
  }

  /** The opcode and fixed modifiers, each once: some opcodes already spell their fixed modifiers. */
  private def spelling(definition: Instruction.Definition): Vec[String] =
    (Vec.from(definition.opcode.raw.split('.')) ++ definition.fixedModifiers.map(_.name.raw)).distinct

  private def kind(constraint: Metadata.OperandConstraint): String = constraint match {
    case Metadata.OperandConstraint.Register      => "register"
    case Metadata.OperandConstraint.Address       => "address"
    case Metadata.OperandConstraint.VectorOperand => "vector"
    case Metadata.OperandConstraint.Symbol        => "symbol"
    case Metadata.OperandConstraint.Value         => "value"
    case Metadata.OperandConstraint.Unknown       => "unknown"
  }

  private def formNames(definition: Instruction.Definition): Vec[String] =
    definition.meta.entries.raw.flatMap(entry => entry.forms.indices.map(index => s"$definition ${index + 1}"))

  private def forms(definition: Instruction.Definition): Vec[View.Form] = definition.meta.entries.raw.flatMap { entry =>
    entry.forms.zipWithIndex.map { (form, index) =>
      val names       = operandNames(definition, form.operands.size)
      val opcode      = spelling(definition).mkString(".")
      val required    = form.requiredModifierGroups.map(group => group.raw.map(_.name.raw).toVec.sorted)
      val optional    = form.optionalModifiers.map(_.name.raw).toVec.sorted
      val typed       = PTXDomains.alternative(definition, form.requiredModifierGroups.map(_.raw))
      val syntaxNames = typed.fold(Vec.empty[Option[String]])(_.names(form.operands.size))
      val slots       = form.requiredModifierGroups.map(group =>
        typed.flatMap(_.required.find(_.values == group.raw)).map(PTXDomains.name(_))
      )
      val syntax = Vec[View.Token](View.Token.Text(opcode)) ++
        required.zip(slots).map((values, slot) =>
          View.Token.Slot(values.mkString(".{", "|", "}"), slot.fold("required modifier")(name => s"modifier $name"))
        ) ++
        Option.when(optional.nonEmpty)(View.Token.Slot(
          optional.mkString("[.", "|.", "]"),
          "optional modifiers"
        )).toVec ++
        names.zip(form.operands).zipWithIndex.flatMap { case ((name, constraint), position) =>
          Vec(View.Token.Text(if (position == 0) " " else ", "), View.Token.Slot(name, kind(constraint)))
        } :+ View.Token.Text(";")
      View.Form(
        s"$definition ${index + 1}",
        syntax,
        names.zip(form.operands).zipWithIndex.map { case ((name, constraint), position) =>
          View.OperandSlot(
            name,
            kind(constraint),
            domain = PTXDomains.operand(definition, typed, position, constraint, syntaxNames.lift(position).flatten)
          )
        },
        required.zip(slots).zipWithIndex.map { case ((values, slot), group) =>
          View.Choice(slot.getOrElse(s"required group ${group + 1}"), values)
        } ++
          Option.when(optional.nonEmpty)(View.Choice("optional", optional)).toVec,
        Vec(
          "Introduced"     -> s"PTX ISA ${entry.introduced}",
          "Minimum target" -> entry.minimumTarget.render
        ) ++
          entry.modifierTargets.toVec
            .map((modifiers, target) => (target.raw, modifiers.map(_.name.raw).toVec.sorted.mkString(".", ".", "")))
            .sorted.map((target, modifiers) => s"With $modifiers" -> s"sm_$target") ++
          Option.when(!form.complete)("Syntax" -> "partially described").toVec
      )
    }
  }

  /** The register declaration each PTX type is held in: its bits where PTX has no register of the type. */
  private def declaration(tpe: String): String = tpe match {
    case "f16" | "bf16" | "e4m3x2" | "e5m2x2" | "e2m3x2" | "e3m2x2" | "e2m1x2" => "b16"
    case "f16x2" | "bf16x2" | "tf32"                                           => "b32"
    case "f32x2"                                                               => "b64"
    case other                                                                 => other
  }

  private val Types = "[bsuf](8|16|32|64)|b128|[bf]16x2|f32x2|bf16|tf32|pred|e[2-5]m[1-3]x2".r

  private def wide(tpe: String): String = tpe match {
    case s"$kind$bits" if bits.toIntOption.nonEmpty => s"$kind${bits.toInt * 2}"
    case other                                      => other
  }

  private def signed(tpe: String): Boolean = tpe.startsWith("s")

  /** `values` with the operand at `index`, where there is one, of type `tpe`. */
  private def at(index: Int, tpe: String)(values: Vec[String]): Vec[String] =
    if (index < values.size) values.updated(index, tpe) else values

  /** The PTX type of each of `count` operands of an instruction with `modifiers`, as the PTX ISA types them: most take
    * the instruction type, and the rest a predicate, a shift or field position, a wide product or a second type.
    */
  def operandTypes(opcode: String, modifiers: Vec[String], count: Int): Vec[String] = {
    val types   = modifiers.filter(Types.matches(_))
    val last    = types.lastOption.getOrElse("b32")
    val first   = types.headOption.getOrElse(last)
    val uniform = Vec.fill(count)(last)
    opcode match {
      case "setp" | "testp"                 => at(3, "pred")(at(0, "pred")(uniform))
      case "set"                            => at(3, "pred")(at(0, first)(uniform))
      case "selp"                           => at(3, "pred")(uniform)
      case "slct"                           => at(3, last)(Vec.fill(count)(first))
      case "cvt" | "cvta"                   => at(0, first)(uniform)
      case "shl" | "shr"                    => at(2, "u32")(uniform)
      case "bfe"                            => at(2, "u32")(at(3, "u32")(uniform))
      case "bfi"                            => at(3, "u32")(at(4, "u32")(uniform))
      case "bfind" | "popc" | "clz" | "fns" => at(0, "u32")(uniform)
      case "dp4a" | "dp2a"                  =>
        val accumulator = if (types.exists(signed(_))) "s32" else "u32"
        at(1, first)(at(2, last)(Vec.fill(count)(accumulator)))
      case "redux"                => at(2, "b32")(uniform)
      case "clusterlaunchcontrol" => at(1, "b128")(at(0, first)(uniform))
      case "mbarrier" if modifiers.exists(Set("test_wait", "try_wait", "check_layout")) => at(0, "pred")(uniform)
      case "match"                                     => at(0, "b32")(at(2, "b32")(uniform))
      case "vote" if modifiers.contains("ballot")      => at(1, "pred")(Vec.fill(count)("b32"))
      case "vote"                                      => at(2, "b32")(Vec.fill(count)("pred"))
      case "mul" | "mad" if modifiers.contains("wide") => at(3, wide(last))(at(0, wide(last))(uniform))
      case _                                           => uniform
    }
  }

  private val Variants = 48

  /** The operand text of an instance and the registers it declares, as the PTX type each holds, or `label` for a label
    * the instance branches to. An operand next to an address carries a vector of the `.vN` modifier's length.
    */
  private def operands(
      mnemonic: String,
      opcode: String,
      modifiers: Vec[String],
      names: Vec[String],
      constraints: Vec[Metadata.OperandConstraint],
      fragments: Vec[Option[Int]]
  ): Option[Vec[(text: String, registers: Vec[(name: String, declared: String)])]] = {
    val types  = operandTypes(opcode, modifiers, names.size)
    val lanes  = modifiers.collectFirst { case s"v$count" if count.toIntOption.nonEmpty => count.toInt }
    val packed = constraints.indices.filter(index =>
      lanes.nonEmpty && !names(index).contains("olicy") &&
        Vec(index - 1, index + 1).exists(constraints.lift(_).contains(Metadata.OperandConstraint.Address))
    ).toSet
    def vector(name: String, tpe: String, count: Int) = {
      val elements = Vec.tabulate(count)(element => s"${name}_$element")
      (elements.mkString("{", ", ", "}"), elements.map(_ -> declaration(tpe)))
    }
    // a trailing cache policy belongs to the .L2::cache_hint forms only; atom.cas takes its comparand there instead,
    // and st.async and red.async their completion mbarrier
    def unused(name: String): Boolean = Set("cachePolicy", "compareOrCachePolicy")(name) &&
      !Set("createpolicy", "st.bulk")(mnemonic) && !modifiers.contains("L2::cache_hint") &&
      !(name.startsWith("compareOr") && modifiers.contains("cas")) &&
      !(Set("st.async", "red.async")(mnemonic) &&
        (modifiers.contains("relaxed") || modifiers.contains("mbarrier::complete_tx::bytes")))
    val halves = modifiers.exists(Set("f16x2", "bf16x2"))
    // destinations written as d|p: a value and a predicate, two predicates, or a wait's completion and report
    val pairs = names.nonEmpty && (
      mnemonic == "elect.sync" || (mnemonic == "lop3" && modifiers.exists(Set("and", "or"))) ||
        (mnemonic == "setp" && halves) ||
        (Set("mbarrier.try_wait", "mbarrier.test_wait")(mnemonic) && modifiers.contains("phase_type::primary") &&
          !constraints.lift(1).contains(Metadata.OperandConstraint.Address))
    )
    // a video operation with a third source and no second operation merges into a part of its destination
    val merge = Video(mnemonic) && names.size == 4 && !modifiers.exists(Set("add", "min", "max"))
    val found = names.zip(constraints).zip(types).zipWithIndex.toVec.filterNot { case (((name, _), _), _) =>
      unused(name)
    }.map { case (((name, constraint), tpe), index) =>
      immediate(mnemonic, modifiers, index).map(value =>
        (text = value, registers = Vec.empty[(name: String, declared: String)])
      )
        .orElse(Option.when(pairs && index == 0)(
          (s"$name|${name}_predicate", Vec(name -> declaration(tpe), s"${name}_predicate" -> "pred"))
        )).orElse(Option.when(merge && index == 0)((s"$name.h0", Vec(name -> declaration(tpe)))))
        .orElse(constraint match {
          case _ if fragments.lift(index).flatten.nonEmpty =>
            Some(vector(name, if (tpe.startsWith("f") && tpe != "f64") "b32" else tpe, fragments(index).getOrElse(1)))
          case Metadata.OperandConstraint.Address       => Some((s"[$name]", Vec(name -> "b64")))
          case Metadata.OperandConstraint.VectorOperand => Some(vector(name, tpe, lanes.getOrElse(4)))
          case _ if opcode == "bra"                     => Some((name, Vec(name -> "label")))
          case _ if mnemonic == "brx.idx" && index == 1 => Some((name, Vec(name -> "branchtargets")))
          case Metadata.OperandConstraint.Symbol | Metadata.OperandConstraint.Unknown => None
          case _ if packed(index) => Some(vector(name, tpe, lanes.getOrElse(1)))
          // a 128-bit register is a pair of 64-bit ones
          case _ if tpe == "b128" => Some(vector(name, "b64", 2))
          case _                  => Some((name, Vec(name -> declaration(tpe))))
        })
    }
    Option.when(found.forall(_.nonEmpty))(found.flatten)
  }

  private val Video = Set("vadd", "vsub", "vabsdiff", "vmin", "vmax", "vshl", "vshr", "vset")

  /** The operands of the bulk asynchronous copies, which the catalogue's forms do not order reliably, as each
    * combination of qualifiers takes them.
    */
  private def bulkOperands(mnemonic: String, modifiers: Vec[String])
      : Option[Vec[(text: String, registers: Vec[(name: String, declared: String)])]] = {
    def has(modifier: String)            = modifiers.contains(modifier)
    def address(name: String)            = (text = s"[$name]", registers = Vec(name -> "b64"))
    def value(name: String, tpe: String) = (text = name, registers = Vec(name -> tpe))
    def when(condition: Boolean)(operands: (text: String, registers: Vec[(name: String, declared: String)])*)
        : Vec[(text: String, registers: Vec[(name: String, declared: String)])] =
      if (condition) Vec.from(operands) else Vec.empty
    val policy = when(has("L2::cache_hint"))(value("cachePolicy", "b64"))
    val mask   = when(has("multicast::cluster"))(value("ctaMask", "b16"))
    val rank   = modifiers.collectFirst { case s"${count}d" if count.length == 1 && count.head.isDigit => count.toInt }
    val tensor = rank.map { count =>
      val coordinates = Vec.tabulate(count)(index => s"coordinate$index")
      (
        text = s"[tensorMap, ${coordinates.mkString("{", ", ", "}")}]",
        registers = ("tensorMap" -> "b64") +: coordinates.map(_ -> "b32")
      )
    }
    val offsets = rank.filter(count => has("im2col") && count > 2).map { count =>
      val names = Vec.tabulate(count - 2)(index => s"im2colOffset$index")
      (text = names.mkString("{", ", ", "}"), registers = names.map(_ -> "b16"))
    }.toVec
    val transfer = Vec(address("dstMem"), address("srcMem"), value("size", "b32"))
    mnemonic match {
      case "cp.async.bulk" if has("bulk_group") =>
        Some(transfer ++ policy ++ when(has("cp_mask"))(value("byteMask", "b16")))
      case "cp.async.bulk" if has("global") =>
        Some(
          transfer ++ when(has("ignore_oob"))(value("ignoreBytesLeft", "b32"), value("ignoreBytesRight", "b32")) ++
            Vec(address("mbar")) ++ mask ++ policy
        )
      case "cp.async.bulk"                 => Some(transfer :+ address("mbar"))
      case "multimem.cp.async.bulk"        => Some(transfer ++ when(has("cp_mask"))(value("byteMask", "b16")))
      case "multimem.cp.reduce.async.bulk" => Some(transfer)
      case "cp.reduce.async.bulk" if has("bulk_group") => Some(transfer ++ policy)
      case "cp.reduce.async.bulk"                      => Some(transfer :+ address("mbar"))
      case "cp.async.bulk.tensor" if has("bulk_group") => tensor.map(map => Vec(map, address("srcMem")) ++ policy)
      case "cp.async.bulk.tensor"                      =>
        tensor.map(map => Vec(address("dstMem"), map, address("mbar")) ++ offsets ++ mask ++ policy)
      case "cp.reduce.async.bulk.tensor"   => tensor.map(map => Vec(map, address("srcMem")) ++ policy)
      case "cp.async.bulk.prefetch.tensor" => tensor.map(map => map +: (offsets ++ policy))
      case _                               => None
    }
  }

  /** An instruction descriptor tcgen05.mma accepts for its qualifiers: M = 128 (256 across a CTA pair), N = 64, and the
    * kind's first element types, accumulating into binary32 or 32-bit integers.
    */
  private def instructionDescriptor(modifiers: Vec[String]): Long = {
    val pair   = modifiers.contains("cta_group::2")
    val sparse = if (modifiers.contains("sp")) 1L << 2 else 0L
    val n      = (64L >>> 3) << 17
    val scaled = (if (pair) 256L >>> 7 else 1L) << 27
    modifiers.collectFirst { case s"kind::$kind" => kind }.getOrElse("f16") match {
      case "tf32"     => sparse | (1L << 4) | (2L << 7) | (2L << 10) | n | ((if (pair) 16L else 8L) << 24)
      case "i8"       => sparse | (2L << 4) | (1L << 7) | (1L << 10) | n | ((if (pair) 16L else 8L) << 24)
      case "mxf8f6f4" => sparse | n | (1L << 23) | scaled
      case "mxf4"     => sparse | (1L << 7) | (1L << 10) | n | (1L << 23) | scaled
      case "mxf4nvf4" => sparse | (1L << 7) | (1L << 10) | n | scaled
      case _          => sparse | (1L << 4) | n | ((if (pair) 16L else 8L) << 24)
    }
  }

  /** The operands of the tcgen05 family, whose roles the catalogue's names do not fix: tensor memory addresses and
    * shared memory descriptors, an immediate instruction descriptor, the enable-input-d predicate, the lanes the
    * register vectors of each data movement shape take, and immediate split and scale offsets.
    */
  private def tensorMemoryOperands(
      mnemonic: String,
      modifiers: Vec[String],
      constraints: Vec[Metadata.OperandConstraint]
  ): Option[Vec[(text: String, registers: Vec[(name: String, declared: String)])]] = {
    def address(name: String)            = (text = s"[$name]", registers = Vec(name -> "b32"))
    def value(name: String, tpe: String) = (text = name, registers = Vec(name -> tpe))
    def literal(text: String)            = (text = text, registers = Vec.empty[(name: String, declared: String)])
    def vector(name: String, count: Int) = {
      val elements = Vec.tabulate(count)(index => s"${name}_$index")
      (text = elements.mkString("{", ", ", "}"), registers = elements.map(_ -> "b32"))
    }
    val repeat = modifiers.collectFirst { case s"x$count" if count.toIntOption.nonEmpty => count.toInt }.getOrElse(1)
    val per    =
      if (modifiers.contains("16x128b")) 2 else if (modifiers.contains("16x256b")) 4 else 1
    import Metadata.OperandConstraint.*
    mnemonic match {
      case "tcgen05.mma" | "tcgen05.mma.sp" | "tcgen05.mma.ws" | "tcgen05.mma.ws.sp" =>
        val sparse   = mnemonic.endsWith(".sp")
        val ws       = mnemonic.contains(".ws")
        val leading  = constraints.take(if (sparse) 5 else 4)
        val trailing = constraints.drop(if (sparse) 5 else 4)
        val head     = Vec(
          address("dTmem"),
          if (leading.lift(1).contains(Address)) address("aTmem") else value("aDescriptor", "b64"),
          value("bDescriptor", "b64")
        ) ++ Option.when(sparse)(address("metadataTmem")).toVec :+
          literal(s"0x${instructionDescriptor(modifiers).toHexString}")
        val twoCta  = modifiers.contains("cta_group::2")
        val scalars = trailing.scanLeft(0)((count, constraint) =>
          constraint match {
            case Address | VectorOperand => count
            case _                       => count + 1
          }
        )
        val tail = trailing.zipWithIndex.map[(text: String, registers: Vec[(name: String, declared: String)])] {
          case (Address, at)               => address(if (at == 0) "scaleATmem" else "scaleBTmem")
          case (VectorOperand, _)          => vector("disabledLanes", if (twoCta) 8 else 4)
          case (_, at) if scalars(at) == 0 => value("enableInputD", "pred")
          case _ if ws                     => value("zeroColumnMask", "b64")
          case _                           => literal("1")
        }
        Some(head ++ tail)
      case "tcgen05.ld" =>
        Some(constraints.zipWithIndex.map {
          case (Register, _)                                               => vector("loaded", repeat * per)
          case (Address, _)                                                => address("tmem")
          case (_, index) if constraints.lift(index + 1).contains(Address) => value("reduced", "b32")
          case _                                                           => literal("1")
        })
      case "tcgen05.st" =>
        Some(constraints.map {
          case Register => vector("stored", repeat * per)
          case Address  => address("tmem")
          case _        => literal("1")
        })
      case "tcgen05.cp"    => Some(Vec(address("tmem"), value("sourceDescriptor", "b64")))
      case "tcgen05.alloc" =>
        Some(Vec((text = "[destination]", registers = Vec("destination" -> "b64")), literal("32")))
      case "tcgen05.dealloc" => Some(Vec(value("tmem", "b32"), literal("32")))
      case _                 => None
    }
  }

  /** The operands of `call`, which name a callee, a function pointer and a prototype, and parameters, which the
    * semantics kernel declares for them: a direct call of a function taking one argument and returning one result as
    * the operands allow, or an indirect call through a prototype.
    */
  private def callOperands(count: Int): Option[Vec[(text: String, registers: Vec[(name: String, declared: String)])]] =
    count match {
      case 1 => Some(Vec((text = "callee", registers = Vec("callee" -> "function(0,0)"))))
      case 2 =>
        Some(Vec(
          (text = "callee", registers = Vec("callee" -> "function(0,1)")),
          (text = "(argument)", registers = Vec("argument" -> "param"))
        ))
      case 3 =>
        Some(Vec(
          (text = "(result)", registers = Vec("result" -> "param")),
          (text = "callee", registers = Vec("callee" -> "function(1,1)")),
          (text = "(argument)", registers = Vec("argument" -> "param"))
        ))
      case 4 =>
        Some(Vec(
          (text = "(result)", registers = Vec("result" -> "param")),
          (text = "pointer", registers = Vec("pointer" -> "b64")),
          (text = "(argument)", registers = Vec("argument" -> "param")),
          (text = "prototype", registers = Vec("prototype" -> "prototype(1,1)"))
        ))
      case _ => None
    }

  /** The value of an operand that must be an immediate: a lookup table, a copy size, an alignment, a pending group
    * count, a fill value or a field ordinal.
    */
  private def immediate(opcode: String, modifiers: Vec[String], index: Int): Option[String] = (opcode, index) match {
    case ("lop3", 4)                     => Some("0x96")
    case ("cp.async", 2)                 => Some("16")
    case ("alloca", 2)                   => Some("8")
    case ("cp.async.bulk.wait_group", 0) => Some("0")
    case ("wgmma.wait_group", 0)         => Some("0")
    case ("st.bulk", 2)                  => Some("0")
    case ("tensormap.cp_fenceproxy", 2)  => Some("128")
    case ("tensormap.replace", 1)
        if modifiers.exists(Set("box_dim", "global_dim", "global_stride", "element_stride")) => Some("0")
    case _ => None
  }

  /** Concrete instructions for a form over registers named after its operands as the operand table names them, each
    * declared as the PTX type it takes: every combination of its required modifier groups, then each value of each
    * group and each optional modifier with the first of the others, up to a bound. Forms that take a function symbol
    * have none.
    */
  def instances(mnemonic: String, formName: String)
      : Vec[(text: String, registers: Vec[(name: String, declared: String)])] =
    Instruction.definitions.filter(_.opcode.raw == mnemonic).flatMap(definition =>
      definition.meta.entries.raw.flatMap(_.forms).zipWithIndex.collect {
        case (form, index) if s"$definition ${index + 1}" == formName =>
          val opcode   = spelling(definition)
          val spelt    = opcode.toSet
          val names    = operandNames(definition, form.operands.size)
          val required =
            form.requiredModifierGroups.map(group => group.raw.map(_.name.raw).toVec.sorted.filterNot(spelt))
          val optional = form.optionalModifiers.map(_.name.raw).toVec.sorted.filterNot(spelt)
          val base     = required.map(_.headOption)
          val product  = required.foldLeft(LazyList(Vec.empty[String]))((partial, values) =>
            partial.flatMap(chosen => if (values.isEmpty) Vec(chosen) else values.map(chosen :+ _))
          ).take(Variants).toVec
          val variants = product ++
            (required.zipWithIndex.flatMap((values, group) =>
              values.drop(1).map(value => base.updated(group, Some(value)).flatten)
            ) ++ optional.map(_ +: base.flatten))
          def instance(modifiers: Vec[String], fragments: Vec[Option[Int]]) =
            bulkOperands(mnemonic, opcode.drop(1) ++ modifiers)
              .orElse(tensorMemoryOperands(mnemonic, opcode.drop(1) ++ modifiers, form.operands))
              .orElse(Option.when(mnemonic == "call")(callOperands(names.size)).flatten).orElse(
                operands(mnemonic, opcode.head, opcode.drop(1) ++ modifiers, names, form.operands, fragments)
              ).map(found =>
                (
                  s"${(opcode ++ modifiers).mkString(".")}${
                      if (found.isEmpty) "" else found.map(_.text).mkString(" ", ", ", "")
                    };",
                  found.flatMap(_.registers)
                )
              )
          variants.distinct.take(Variants * 2).zipWithIndex.flatMap((modifiers, index) =>
            if (mnemonic.startsWith("wgmma.mma_async"))
              warpgroup(mnemonic, opcode ++ modifiers, form.operands.size, alternate = index % 2 == 1)
            else
              instance(modifiers, Vec.empty).flatMap(plain =>
                InstructionSemantics.fragments(plain._1).fold(Some(plain))(instance(modifiers, _))
              ).toVec
          )
      }.flatten
    )

  /** Instances of a `wgmma.mma_async` form, whose catalogue does not tell a descriptor from a register operand or an
    * immediate: A at a descriptor and, where the form's operand count allows, in registers, with a predicate scale-d, a
    * negated A and an MN-major A, or the immediate scale-d, a negated B and an MN-major B where `alternate`.
    */
  private def warpgroup(mnemonic: String, spelt: Vec[String], count: Int, alternate: Boolean)
      : Vec[(text: String, registers: Vec[(name: String, declared: String)])] = {
    val sparse   = mnemonic.endsWith(".sp")
    val types    = spelt.filter(Set("f16", "bf16", "tf32", "e4m3", "e5m2", "s8", "u8", "b1", "f32", "s32"))
    val halves   = types.lift(1).exists(Set("f16", "bf16"))
    val floating = !types.lift(1).exists(Set("s8", "u8", "b1"))
    val dense    = 4 + (if (sparse) 2 else 0) + (if (floating) 2 else 0)
    val kinds    = if (halves) Vec(count > dense + 1) else Vec(true, false)
    val opcode   = spelt.mkString(".")
    val none     = Vec.empty[(String, String)]
    val scale    = if (alternate) ("1", none) else ("scaleD", Vec("scaleD" -> "pred"))
    kinds.flatMap { descriptor =>
      val a =
        if (descriptor) ("aDesc", Vec("aDesc" -> "b64"))
        else ("{a_0, a_1, a_2, a_3}", Vec.tabulate(4)(index => s"a_$index" -> "b32"))
      def sign(negated: Boolean): String = if (negated) "-1" else "1"
      val signs                          = if (floating) Vec(sign(!alternate), sign(alternate)) else Vec.empty[String]
      val trans                          =
        if (!halves) Vec.empty[String]
        else (if (descriptor) Vec(if (alternate) "0" else "1") else Vec.empty[String]) :+ (if (alternate) "1" else "0")
      val selector =
        if (alternate && !types.lift(1).exists(PartialFunction.cond(_) { case "e4m3" | "e5m2" | "s8" | "u8" => true }))
          "1"
        else "0"
      val sparsity =
        if (sparse) Vec(("spMeta", Vec("spMeta" -> "b32")), (selector, none))
        else Vec.empty[(String, Vec[(String, String)])]
      val rest =
        Vec(a, ("bDesc", Vec("bDesc" -> "b64"))) ++ sparsity ++ (scale +: (signs ++ trans).map(_ -> none))
      def text(d: String): String = s"$opcode ${(d +: rest.map(_._1)).mkString(", ")};"
      InstructionSemantics.fragments(text("{d_0}")).flatMap(_.headOption.flatten).map { registers =>
        val d = Vec.tabulate(registers)(index => s"d_$index")
        (
          text = text(d.mkString("{", ", ", "}")),
          registers = (d.map(_ -> "b32") ++ rest.flatMap(_._2)).map((name, declared) =>
            (name = name, declared = declared)
          )
        )
      }
    }
  }

  private val cache = mutable.Map.empty[Option[String], Vec[View.Entry]]

  def entries(target: Option[String]): Vec[View.Entry] = cache.getOrElseUpdate(
    target, {
      val selected = target.flatMap(Target.parse(_).toOption)
      Instruction.definitions
        .filter(definition => selected.forall(target => minimumTargets(definition).exists(_ <= target)))
        .groupBy(_.opcode.raw).toVec.sortBy(_._1)
        .map((mnemonic, definitions) =>
          View.Entry(Language.PTX, mnemonic, definitions.flatMap(formNames(_)), () => definitions.flatMap(forms(_)))
        )
    }
  )

  private def special(name: String, note: String): View.Register = View.Register(name, note = note)

  def registers(target: Option[String]): Vec[View.RegisterFile] = Vec(
    View.RegisterFile(
      "Virtual registers",
      "Declared per function with .reg and a type (.pred, .b16, .u32, .f64 and so on); PTX does not bound their count.",
      Vec.empty
    ),
    View.RegisterFile(
      "Special registers",
      "Read-only, predefined registers from the PTX ISA; ptx-asm parses them as ordinary register operands.",
      Vec(
        special("%tid", "thread index within the CTA (.x, .y, .z)"),
        special("%ntid", "CTA dimensions (.x, .y, .z)"),
        special("%laneid", "lane within the warp"),
        special("%warpid", "warp within the CTA; may change during execution"),
        special("%nwarpid", "maximum number of warp identifiers"),
        special("%ctaid", "CTA index within the grid (.x, .y, .z)"),
        special("%nctaid", "grid dimensions in CTAs (.x, .y, .z)"),
        special("%smid", "multiprocessor identifier"),
        special("%nsmid", "maximum number of multiprocessor identifiers"),
        special("%gridid", "grid launch identifier"),
        special("%is_explicit_cluster", "whether the launch specified a cluster"),
        special("%clusterid", "cluster index within the grid (.x, .y, .z)"),
        special("%nclusterid", "grid dimensions in clusters (.x, .y, .z)"),
        special("%cluster_ctaid", "CTA index within the cluster (.x, .y, .z)"),
        special("%cluster_nctaid", "cluster dimensions in CTAs (.x, .y, .z)"),
        special("%cluster_ctarank", "CTA rank within the cluster"),
        special("%cluster_nctarank", "number of CTAs in the cluster"),
        special("%lanemask_eq", "bit set at the lane's own position"),
        special("%lanemask_le", "bits set at positions up to and including the lane"),
        special("%lanemask_lt", "bits set at positions below the lane"),
        special("%lanemask_ge", "bits set at positions from the lane upwards"),
        special("%lanemask_gt", "bits set at positions above the lane"),
        special("%clock", "32-bit cycle counter"),
        special("%clock_hi", "upper 32 bits of %clock64"),
        special("%clock64", "64-bit cycle counter"),
        special("%pm0..%pm7", "32-bit performance monitoring counters"),
        special("%pm0_64..%pm7_64", "64-bit performance monitoring counters"),
        special("%envreg0..%envreg31", "driver-defined environment registers"),
        special("%globaltimer", "64-bit nanosecond timer"),
        special("%globaltimer_lo", "lower 32 bits of %globaltimer"),
        special("%globaltimer_hi", "upper 32 bits of %globaltimer"),
        special("%reserved_smem_offset_begin", "start of reserved shared memory"),
        special("%reserved_smem_offset_end", "end of reserved shared memory"),
        special("%reserved_smem_offset_cap", "capacity of reserved shared memory"),
        special("%total_smem_size", "shared memory allocated to the CTA"),
        special("%aggr_smem_size", "shared memory allocated to the CTA, including reserved memory"),
        special("%dynamic_smem_size", "dynamically allocated shared memory"),
        special("%current_graph_exec", "handle of the executing CUDA graph")
      )
    )
  )

  val binary: Boolean = false

  def assemble(source: String, target: Option[String]): Either[String, String] =
    Parser.parse(source).left.map(_.diagnostic.raw).map { program =>
      val issues = ISA.validate(program) match {
        case ValidationOutcome.Accepted           => Vec.empty[String]
        case ValidationOutcome.Rejected(issues)   => issues.raw.map(issue => s"// rejected: ${issue.message}")
        case ValidationOutcome.Incomplete(issues) => issues.raw.map(issue => s"// incomplete: ${issue.message}")
      }
      (issues :+ Printer.render(program)).mkString("\n")
    }

  def disassemble(bytes: Vec[Byte], target: Option[String]): Either[String, String] =
    ISA.decode(bytes).left.map(_.message.raw).map(Printer.render(_))

  val assemblyExample: String =
    """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry kernel(.param .u64 input) {
      |  .reg .u32 %r<2>;
      |  mov.u32 %r0, %tid.x;
      |  add.u32 %r1, %r0, 1;
      |  ret;
      |}
      |""".stripMargin
  val binaryExample: String = ""

  private val machined =
    ConcurrentHashMap[(View.Entry, View.Form), Vec[(instance: String, outcome: Either[Unrendered, Definition])]]()

  override protected def clearOutcomes(): Unit = machined.clear()

  override def semanticsOf(entry: View.Entry, form: View.Form): Semantics =
    orderingOnly.semantics(entry.mnemonic, form.name).getOrElse(machineSemantics(entry, form))

  private def machineSemantics(entry: View.Entry, form: View.Form): Semantics = {
    val outcomes = machined.computeIfAbsent(
      (entry, form),
      _ =>
        instances(entry.mnemonic, form.name).flatMap((instance, registers) =>
          InstructionSemantics.machine(entry.mnemonic, instance, registers).map((instance, _))
        )
    )
    withMachineOutcomes(entry, form, outcomes)
  }

  protected def render(entry: View.Entry, form: View.Form): Either[Unavailable, Vec[Described]] = {
    val results = instances(entry.mnemonic, form.name).map((instance, registers) =>
      instance -> InstructionSemantics.describe(entry.mnemonic, instance, registers)
    )
    val defined = results.flatMap((instance, result) => result.toOption.map(Described(instance, _)))
    Either.cond(
      defined.nonEmpty,
      defined,
      if (results.isEmpty) Unavailable.NotInstantiated("No instance of the form is valid PTX.")
      else NvidiaReasons.of(entry.mnemonic, results.flatMap(_._2.left.toOption))
    )
  }
}
