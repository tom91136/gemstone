package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.NVDisasm.*

/** Evaluates recovered operand constraints; unknown conditions never become permission to execute. */
object Legality {

  /** Add the kind aliases used by FORMAT conditions to the normalized encoding bindings. Decoder and assembler inputs
    * use encoded field names, while recovered constraints may refer to the field's declared FORMAT binding or kind.
    * Keep these aliases in one place so both paths evaluate the same condition against the same semantic values.
    */
  def completeBindings(form: NVDisasm.Form, bindings: Map[String, BigInt]): Map[String, BigInt] = {
    val declared     = SASSFormatBindings.fields(form.syntax)
    val kindBindings = SASSFormatBindings.kindBindings(declared)
    declared.foldLeft(bindings) { (current, field) =>
      val value = current.get(field.binding).orElse {
        Option.when(kindBindings.get(field.kind).contains(field.binding))(current.get(field.kind)).flatten
      }
      value.fold(current) { raw =>
        val withBinding = current.updated(field.binding, raw)
        if (kindBindings.get(field.kind).contains(field.binding)) withBinding.updated(field.kind, raw)
        else withBinding
      }
    }
  }

  def check(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      bindings: Map[String, BigInt]
  ): Either[NVDisasm.CandidateFailure, Unit] = {
    val conditions = form.conditions
      .filter(_.isError)
      .filterNot(condition => condition.isScheduling || unobservableScoreboardEncoding(condition, bindings))
    conditions.foldLeft[Either[NVDisasm.CandidateFailure, Unit]](Right(())) { (result, condition) =>
      result.flatMap(_ =>
        evaluate(database, condition.expression, bindings).left.map(message =>
          NVDisasm.CandidateFailure(NVDisasm.FailureCategory.Unsupported, message)
        ).flatMap { value =>
          Either.cond(
            value != 0,
            (),
            NVDisasm.CandidateFailure(
              NVDisasm.FailureCategory.IllegalCandidate,
              s"illegal ${form.name}: ${condition.message}"
            )
          )
        }
      )
    }
  }

  /** The legacy MIO_RD_SCBD constraint refers to a pre-encoding scoreboard value that is not recoverable from these
    * instruction bits: VarLatOperandEnc maps both 7 and 65535 to the same encoded value 7. When decoding a machine
    * word, absence of dst_wr_sb is therefore expected. If a caller does provide that semantic value, keep checking the
    * recovered constraint normally.
    */
  private def unobservableScoreboardEncoding(condition: NVDisasm.Condition, bindings: Map[String, BigInt]): Boolean =
    condition.exception == NVDisasm.ConditionException.UnobservableScoreboardEncoding && !bindings.contains("dst_wr_sb")

  def evaluate(
      database: NVDisasm.Database,
      expression: NVDisasm.ConditionExpression,
      bindings: Map[String, BigInt]
  ): Either[String, BigInt] = {
    import NVDisasm.ConditionExpression.*
    def truth(value: Boolean): BigInt                                     = if (value) BigInt(1) else BigInt(0)
    def eval(value: NVDisasm.ConditionExpression): Either[String, BigInt] = evaluate(database, value, bindings)
    def values(arguments: Vec[NVDisasm.ConditionExpression]): Either[String, Vec[BigInt]] =
      arguments.foldLeft[Either[String, Vec[BigInt]]](Right(Vec.empty)) { (result, argument) =>
        for {
          previous <- result
          value    <- eval(argument)
        } yield previous :+ value
      }
    expression match {
      case Number(value)                               => Right(value)
      case Conditional(condition, whenTrue, whenFalse) =>
        eval(condition).flatMap(value => eval(if (value != 0) whenTrue else whenFalse))
      case Field(name, attribute) =>
        val key = attribute.fold(name)(suffix => s"$name@$suffix")
        bindings.get(key).orElse(
          Option.when(attribute.nonEmpty && bindings.contains(name))(BigInt(0))
        ).orElse(database.constants.get(key)).toRight(s"unsupported condition reference $key")
      case Parameter(name)       => database.parameters.get(name).toRight(s"unsupported condition parameter $name")
      case Constant(name)        => database.constants.get(name).toRight(s"unsupported condition constant $name")
      case EnumValue(kind, name) => database.byVocabulary.get(kind).orElse {
          database.symbols.filter(_.name.equalsIgnoreCase(kind)) match {
            case Vec(vocabulary) => Some(vocabulary)
            case _               => None
          }
        }.flatMap(_.values.find(_.name == name)).map(_.value)
          .toRight(s"unsupported condition enum $kind@$name")
      case Unary(UnaryOperator.LogicalNot, value)         => eval(value).map(value => truth(value == 0))
      case Unary(UnaryOperator.Negate, value)             => eval(value).map(-_)
      case Unary(UnaryOperator.BitwiseNot, value)         => eval(value).map(~_)
      case Unary(UnaryOperator.Unsupported(operator), _)  => Left(s"unsupported condition operator $operator")
      case Binary(BinaryOperator.LogicalAnd, left, right) =>
        eval(left).flatMap(value => if (value == 0) Right(BigInt(0)) else eval(right).map(value => truth(value != 0)))
      case Binary(BinaryOperator.LogicalOr, left, right) =>
        eval(left).flatMap(value => if (value != 0) Right(BigInt(1)) else eval(right).map(value => truth(value != 0)))
      case Binary(BinaryOperator.Implies, left, right) =>
        eval(left).flatMap(value => if (value == 0) Right(BigInt(1)) else eval(right).map(value => truth(value != 0)))
      case Binary(operator, left, right) => for {
          a      <- eval(left)
          b      <- eval(right)
          result <- operator match {
            case BinaryOperator.Equal                           => Right(truth(a == b))
            case BinaryOperator.NotEqual                        => Right(truth(a != b))
            case BinaryOperator.Less                            => Right(truth(a < b))
            case BinaryOperator.LessEqual                       => Right(truth(a <= b))
            case BinaryOperator.Greater                         => Right(truth(a > b))
            case BinaryOperator.GreaterEqual                    => Right(truth(a >= b))
            case BinaryOperator.Add                             => Right(a + b)
            case BinaryOperator.Subtract                        => Right(a - b)
            case BinaryOperator.Multiply                        => Right(a * b)
            case BinaryOperator.Remainder if b != 0             => Right(a % b)
            case BinaryOperator.Divide if b != 0                => Right(a / b)
            case BinaryOperator.BitwiseAnd                      => Right(a & b)
            case BinaryOperator.BitwiseOr                       => Right(a | b)
            case BinaryOperator.BitwiseXor                      => Right(a ^ b)
            case BinaryOperator.ShiftLeft if b >= 0 && b < 128  => Right(a << b.toInt)
            case BinaryOperator.ShiftRight if b >= 0 && b < 128 => Right(a >> b.toInt)
            case BinaryOperator.Unsupported(operator)           => Left(s"unsupported condition operator $operator")
            case _ => Left(s"unsupported condition operator ${operator.spelling}")
          }
        } yield result
      case Call(BuiltinCall.Defined, Vec(Call(BuiltinCall.Table(name), arguments))) => for {
          table <- database.byTable.get(name).toRight(s"unsupported condition table $name")
          _     <-
            Either.cond(table.issues.isEmpty, (), s"unsupported condition table $name: ${table.issues.mkString("; ")}")
          inputs <- values(arguments)
        } yield truth(table.rows.exists(_.inputs == inputs))
      case Call(name, Vec(argument)) if name == BuiltinCall.IsEven || name == BuiltinCall.IsOdd =>
        eval(argument).map { value =>
          truth(if (name == BuiltinCall.IsEven) value % 2 == 0 else value % 2 != 0)
        }
      case Call(BuiltinCall.Table(name), arguments) => for {
          table <- database.byTable.get(name).toRight(s"unsupported condition call $name")
          _     <-
            Either.cond(table.issues.isEmpty, (), s"unsupported condition table $name: ${table.issues.mkString("; ")}")
          inputs <- values(arguments)
          matches = table.rows.filter(_.inputs == inputs).map(_.output).distinct
          result <- Either.cond(
            matches.size == 1,
            matches.headOption.getOrElse(BigInt(0)),
            s"undefined or ambiguous condition lookup $name"
          )
        } yield result
      case Call(BuiltinCall.Defined, _) => Left("unsupported condition call DEFINED")
      case _                            => Left(s"unsupported condition $expression")
    }
  }
}
