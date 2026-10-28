package net.kurobako.gemstone.isa.ptx

import Program.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Resolve translation-unit scope before lowering concatenated PTX linker inputs. */
private object Linker {
  def preferDefinition[A <: Statement.Data | Statement.Function | Statement.Entry](previous: A, next: A): A =
    if (definitionPriority(next) > definitionPriority(previous)) next else previous

  private def definitionPriority(value: Statement.Data | Statement.Function | Statement.Entry): Int = {
    val (linkage, defined) = value match {
      case data: Statement.Data =>
        val linkage = Linkage.values.find(linkage =>
          linkage != Linkage.Default && data.qualifiers.exists {
            case DeclarationQualifier.Attribute(name) => name.raw == linkage.mnemonic
            case _                                    => false
          }
        ).getOrElse(Linkage.Default)
        (linkage, linkage != Linkage.Extern)
      case function: Statement.Function => (function.linkage, function.body.nonEmpty)
      case entry: Statement.Entry       => (entry.linkage, entry.body.nonEmpty)
    }
    if (!defined) 0 else if (linkage == Linkage.Weak) 1 else 2
  }

  def link(program: Program): Either[Fault, Program] = {
    val units = modules(program.statements)
    if (units.size <= 1) Right(program)
    else {
      val names  = program.statements.flatMap(declarationName(_)).toSet
      val prefix = Iterator.from(0).map(index => s"$$gemstone$$$index$$").find(prefix =>
        names.forall(name => !name.raw.startsWith(prefix))
      ).get
      units.zipWithIndex.traverse { case (statements, index) =>
        val names = statements.collect {
          case value: Statement.Data if !externallyVisible(value.qualifiers) => value.name
          case Statement.Function(linkage = Linkage.Default, name = name)    => name
          case Statement.Entry(linkage = Linkage.Default, name = name)       => name
        }.map { name =>
          val scoped: NonEmpty[String] = s"$prefix$index$$$name"
          name -> scoped
        }.toMap
        Renaming(names).statements(statements)
      }.map(statements => Program(statements.flatten))
    }
  }

  private def externallyVisible(qualifiers: Vec[DeclarationQualifier]): Boolean =
    qualifiers.exists {
      case DeclarationQualifier.Attribute(name) =>
        Linkage.values.exists(linkage => linkage != Linkage.Default && linkage.mnemonic == name.raw)
      case _ => false
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def modules(statements: Vec[Statement]): Vec[Vec[Statement]] = {
    val result      = Vec.newBuilder[Vec[Statement]]
    val current     = mutable.ArrayBuffer.empty[Statement]
    var versionSeen = false
    statements.foreach { statement =>
      statement match {
        case Statement.Directive(path = path) if path.raw == Vec("version") =>
          if (versionSeen) { result += Vec.from(current); current.clear() }
          versionSeen = true
        case _ => ()
      }
      current += statement
    }
    result += Vec.from(current)
    result.result()
  }

  private def declarationName(statement: Statement): Option[NonEmpty[String]] = statement match {
    case value: Statement.Data          => Some(value.name)
    case value: Statement.Function      => Some(value.name)
    case value: Statement.Entry         => Some(value.name)
    case Statement.Label(name)          => Some(name)
    case value: Statement.CallPrototype => Some(value.name)
    case _                              => None
  }

  private class Renaming(names: Map[NonEmpty[String], NonEmpty[String]]) {
    private def rename(name: NonEmpty[String]): NonEmpty[String] = names.getOrElse(name, name)

    private def operand(value: Operand): Operand = value match {
      case Operand.Symbol(name) => Operand.Symbol(rename(name))
      case other                => other.mapChildren(operand(_))
    }

    private def predication(value: Predication): Predication = value.copy(operand = operand(value.operand))

    private def directive(value: Statement.Directive): Statement.Directive = value.copy(
      arguments = value.arguments.map(argument => argument.copy(operand = operand(argument.operand))),
      initialiser = value.initialiser.map(operand(_))
    )

    private def declarationOperands(value: Operand): Vec[NonEmpty[String]] = value match {
      case Operand.Symbol(name)         => Vec(name)
      case Operand.Index(base, _)       => declarationOperands(base)
      case Operand.VectorLiteral(items) => items.flatMap(declarationOperands(_))
      case _                            => Vec.empty
    }

    def statements(values: Vec[Statement]): Either[Fault, Vec[Statement]] = values.traverse(statement(_))

    def localStatements(values: Vec[Statement]): Either[Fault, Vec[Statement]] = {
      val locals = values.flatMap {
        case Statement.Directive(path, arguments, _, _)
            if PartialFunction.cond(path.raw.head.raw) { case "reg" | "local" | "param" => true } =>
          arguments.flatMap(argument => declarationOperands(argument.operand))
        case statement => Vec.from(declarationName(statement))
      }.toSet
      val scoped = if (locals.isEmpty) this else Renaming(names -- locals)
      values.traverse(scoped.statement(_))
    }

    private def statement(value: Statement): Either[Fault, Statement] = value match {
      case data: Statement.Data =>
        Right(data.copy(name = rename(data.name), initialiser = data.initialiser.map(operand(_))))
      case value: Statement.Directive         => Right(directive(value))
      case Statement.Instruction(instruction) =>
        instruction.decoder(instruction.operands.map(operand(_)), instruction.mods)
          .leftMap(error => Fault.InvalidRequest(s"PTX linker could not rewrite ${instruction.opcode}: $error"))
          .map(form =>
            Statement.Instruction(instruction.copy(form = form, pred = instruction.pred.map(predication(_))))
          )
      case value: Statement.UnsupportedInstruction => Right(value.copy(
          operands = value.operands.map(operand(_)),
          pred = value.pred.map(predication(_))
        ))
      case Statement.Block(body)     => localStatements(body).map(Statement.Block(_))
      case value: Statement.Function =>
        val parameters = (value.parameters ++ value.returnParameters).map(_.name).toSet
        val scope      = Renaming(names -- parameters)
        value.body.traverse(scope.localStatements(_)).map(body =>
          value.copy(
            name = rename(value.name),
            body = body,
            directives = value.directives.map(scope.directive(_))
          )
        )
      case value: Statement.Entry =>
        val scope = Renaming(names -- value.parameters.map(_.name).toSet)
        value.body.traverse(scope.localStatements(_)).map(body =>
          value.copy(
            name = rename(value.name),
            body = body,
            directives = value.directives.map(scope.directive(_))
          )
        )
      case value: Statement.CallPrototype => Right(value.copy(directives = value.directives.map(directive(_))))
      case other                          => Right(other)
    }
  }
}
