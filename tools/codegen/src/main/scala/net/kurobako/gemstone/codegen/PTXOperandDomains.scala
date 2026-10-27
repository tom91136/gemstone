package net.kurobako.gemstone.codegen

/** Derives the typed operand and modifier domains of the PTX instruction set from the syntax blocks, the type tables
  * and the special register listings of the pinned document, and the prose rules of [[PTXOperandRules]].
  */
object PTXOperandDomains {

  /** One `.name` position of an opcode spelling and the values it takes. */
  case class Slot(name: String, values: Vector[String])
  case class Operand(name: String, constraint: String, optional: Boolean)
  case class FormDomains(
      required: Vector[Slot],
      optional: Vector[Slot],
      unordered: Vector[String],
      complete: Boolean,
      operands: Vector[Operand]
  )
  case class Definition(caseName: String, opcode: String, forms: Vector[FormDomains])

  /** A PTX type: `kind` is `b`, `s`, `u`, `f` or `pred` for scalars, `packed` for element pairs and quadruples and
    * `alt` for formats without a register class of their own. `declared` lists the types a register holding it is
    * declared with.
    */
  case class DataType(
      name: String,
      kind: String,
      bits: Int,
      elements: Int,
      element: Option[String],
      declared: Vector[String],
      source: String
  )

  case class Derived(
      definitions: Vector[Definition],
      types: Vector[DataType],
      tables: PTXReferenceTables.Tables,
      rules: PTXOperandRules.Rules,
      findings: Vector[DomainFinding]
  ) {

    /** The distinct slots, ordered by name then values. */
    lazy val slots: Vector[Slot] =
      definitions.flatMap(_.forms).flatMap(form => form.required ++ form.optional).distinct.sortBy(slot =>
        (slot.name, slot.values.mkString(","))
      )

    /** The slot names that take more than one value somewhere, which become the typed [[ModifierSlot]] cases. */
    lazy val namedSlots: Vector[String] = slots.filter(_.values.size > 1).map(_.name).distinct.sorted
  }

  private val Counted = "(.*?)<([0-9]+)>(.*)".r

  def derive(
      definitions: Vector[Definition],
      tables: PTXReferenceTables.Tables,
      rules: PTXOperandRules.Rules,
      citations: Vector[String]
  ): Derived = {
    val (types, duplicates) = dataTypes(tables, rules)
    val known               = types.map(_.name).toSet
    val findings            = duplicates ++
      citations.map(DomainFinding("citation", _, "the cited sentence is not in the document", None)) ++
      tableFindings(types, tables, rules) ++ identifierFindings(tables, rules) ++
      slotFindings(definitions, known, rules)
    Derived(definitions, types, tables, rules, findings)
  }

  private def dataTypes(
      tables: PTXReferenceTables.Tables,
      rules: PTXOperandRules.Rules
  ): (Vector[DataType], Vector[DomainFinding]) = {
    val packedNames = tables.packed.map(_.name).toSet
    val scalars     = tables.fundamental.flatMap { (basic, names) =>
      val kind = basic match {
        case "Signed integer"   => "s"
        case "Unsigned integer" => "u"
        case "Floating-point"   => "f"
        case "Bits (untyped)"   => "b"
        case "Predicate"        => "pred"
        case other              => throw IllegalStateException(s"unknown fundamental type class $other")
      }
      names.collect {
        case name if !packedNames(name) =>
          val bits = if (kind == "pred") 1 else name.dropWhile(_.isLetter).toInt
          DataType(name, kind, bits, 1, None, Vector(name), "Fundamental Type Specifiers")
      }
    }
    val widths = scalars.map(scalar => scalar.name -> scalar.bits).toMap
    val packed = tables.packed.map { row =>
      val bits = row.declared.flatMap(widths.get).headOption.getOrElse(0)
      DataType(
        row.name,
        "packed",
        bits,
        row.elements,
        Some(row.element),
        row.declared,
        "Packed Floating Point Data Types"
      )
    }
    val extra = rules.types.map(row =>
      DataType(row.name, row.kind, row.bits, row.elements, row.element, row.declared, row.cite.section)
    )
    val twice = tables.fundamental.flatMap(_.types).filter(packedNames).map { name =>
      DomainFinding(
        "type-listed-twice",
        s".$name",
        "the fundamental type specifiers and the packed type table both list it",
        Some("the packed table decides, as it gives the element type and the declaration")
      )
    }
    val declared = (scalars ++ packed ++ extra).map(_.name).toSet
    val elements = tables.packed.map(_.element).distinct.filterNot(declared).map { name =>
      DataType(name, "alt", elementBits(name), 1, None, Vector.empty, "Packed Floating Point Data Types")
    }
    (scalars ++ packed ++ extra ++ elements, twice)
  }

  private val Exponent = "e([0-9])m([0-9])".r
  private val Unsigned = "ue([0-9])m([0-9])".r

  private def elementBits(name: String): Int = name match {
    case Exponent(exponent, mantissa) => 1 + exponent.toInt + mantissa.toInt
    case Unsigned(exponent, mantissa) => exponent.toInt + mantissa.toInt
    case _                            => 8
  }

  /** Types the relaxed tables use must be known, and same-size pairs must agree with the type checking table. */
  private def tableFindings(
      types: Vector[DataType],
      tables: PTXReferenceTables.Tables,
      rules: PTXOperandRules.Rules
  ): Vector[DomainFinding] = {
    val scalar =
      types.collect { case row if row.elements == 1 && row.kind != "pred" => row.name -> (row.kind, row.bits) }
        .toMap
    def unknown(label: String, matrix: PTXReferenceTables.Matrix): Vector[DomainFinding] =
      (matrix.columns ++ matrix.rows.map(_.label)).distinct.filterNot(scalar.contains).map { name =>
        DomainFinding("table-type", s"$label .$name", "a relaxed table names a type the type tables do not list", None)
      }
    def agreement(label: String, matrix: PTXReferenceTables.Matrix): Vector[DomainFinding] = for {
      (instruction, cells) <- matrix.rows
      (operand, cell)      <- matrix.columns.zip(cells)
      (ikind, ibits)       <- scalar.get(instruction).toVector
      (okind, obits)       <- scalar.get(operand).toVector
      expected             <- tables.agreement(s"${ikind}X", s"${okind}X").toVector
      if ibits == obits && (expected == "invalid") != (cell == "inv")
    } yield DomainFinding(
      "agreement",
      s"$label .$instruction/.$operand",
      s"same-size pair is $cell but the type checking table says $expected",
      rules.resolution("agreement", s".$instruction/.$operand")
    )
    unknown("source", tables.source) ++ unknown("destination", tables.destination) ++
      agreement("source", tables.source) ++ agreement("destination", tables.destination)
  }

  private def expand(name: String): Vector[String] = name match {
    case Counted(prefix, count, suffix) => Vector.tabulate(count.toInt)(index => s"$prefix$index$suffix")
    case other                          => Vector(other)
  }

  private def identifierFindings(
      tables: PTXReferenceTables.Tables,
      rules: PTXOperandRules.Rules
  ): Vector[DomainFinding] = {
    val listed = tables.identifiers.flatMap {
      case "%pm0, ..., %pm7" => Vector.tabulate(8)(index => s"%pm$index")
      case other             => expand(other)
    }.filter(_.startsWith("%")).toSet
    val described = tables.special.map(_.name.takeWhile(_ != '.')).toSet
    def missing(check: String, subjects: Set[String], where: String): Vector[DomainFinding] =
      subjects.toVector.sorted.map(name =>
        DomainFinding(check, name, where, rules.resolution(check, name))
      )
    missing(
      "identifier-undescribed",
      listed -- described,
      "the predefined identifier table lists it, no special register section declares it"
    ) ++
      missing(
        "identifier-unlisted",
        described -- listed,
        "a special register section declares it, the predefined identifier table does not list it"
      )
  }

  /** Values of type slots must be known types. */
  private def slotFindings(
      definitions: Vector[Definition],
      known: Set[String],
      rules: PTXOperandRules.Rules
  ): Vector[DomainFinding] = {
    val slots = for {
      definition <- definitions
      form       <- definition.forms
      slot       <- form.required ++ form.optional
    } yield definition.opcode -> slot
    val typed   = slots.filter((_, slot) => slot.name.endsWith("type") && !slot.values.forall(_.startsWith(slot.name)))
    val offered = typed.map(_._2).foldLeft(Set.empty[String])((seen, slot) =>
      if (slot.values.size > 1) seen ++ slot.values else seen
    )
    val values = offered.filterNot(known).toVector.sorted.map(value =>
      DomainFinding(
        "slot-type",
        s".$value",
        "a type slot takes it but no type table lists it",
        rules.resolution("slot-type", value)
      )
    )
    val undefined = typed.filter((_, slot) => slot.values == Vector(slot.name)).map(_._1).distinct.sorted.map(opcode =>
      DomainFinding(
        "slot-undefined",
        s"$opcode .type",
        "the syntax block names a type slot it does not define",
        rules.resolution("slot-undefined", opcode)
      )
    )
    values ++ undefined
  }
}
