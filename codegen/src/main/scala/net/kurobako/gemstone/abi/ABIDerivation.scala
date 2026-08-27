package net.kurobako.gemstone.abi

import scala.quoted.Expr
import scala.quoted.Quotes
import scala.quoted.Type
import scala.quoted.Varargs

object ABIDerivation {
  inline def methods[T]: Vector[ABIMethod]              = ${ methodsImpl[T] }
  inline def cFunctions[T]: Vector[CFunction]           = ${ cFunctionsImpl[T] }
  inline def nativeDelegates[T]: Vector[ABIDeclaration] = ${ nativeDelegatesImpl[T] }

  private def nativeDelegatesImpl[T: Type](using quotes: Quotes): Expr[Vector[ABIDeclaration]] = {
    import quotes.reflect.*
    val interface = TypeRepr.of[T].typeSymbol
    if (!interface.flags.is(Flags.Trait)) report.errorAndAbort(s"ABI interface must be a trait: ${interface.fullName}")
    val methods = publicMethods(interface).filter(method => nativeDelegate(method).nonEmpty)
    rejectOverloads(methods)
    val expressions = methods.map { method =>
      val signature = annotationString(method, "net.kurobako.gemstone.abi.CSignature", "CSignature")
        .getOrElse(abortAt(method, s"native delegate ${method.name} is missing CSignature"))
      val parameterNames = method.paramSymss.flatten.map(parameter => Expr(parameter.name))
      val target         = nativeDelegate(method).get
      '{
        ABIDeclaration(
          ${ Expr(method.name) },
          ${ Expr(signature) },
          Vector(${ Varargs(parameterNames) }*),
          MethodImplementation.NativeDelegate(${ Expr(target) })
        )
      }
    }
    '{ Vector(${ Varargs(expressions) }*) }
  }

  private def cFunctionsImpl[T: Type](using quotes: Quotes): Expr[Vector[CFunction]] = {
    import quotes.reflect.*

    val interface = TypeRepr.of[T].typeSymbol
    if (!interface.flags.is(Flags.Trait)) report.errorAndAbort(s"ABI interface must be a trait: ${interface.fullName}")
    val methods = publicMethods(interface).filter(method =>
      method.annotations.exists(_.tpe.typeSymbol.fullName == "net.kurobako.gemstone.abi.CSignature")
    )
    rejectOverloads(methods)
    val expressions = methods.map { method =>
      val signature = annotationString(method, "net.kurobako.gemstone.abi.CSignature", "CSignature")
        .getOrElse(abortAt(method, s"ABI method ${method.name} is missing CSignature"))
      '{ CFunction(${ Expr(method.name) }, ${ Expr(signature) }) }
    }
    '{ Vector(${ Varargs(expressions) }*) }
  }

  private def methodsImpl[T: Type](using quotes: Quotes): Expr[Vector[ABIMethod]] = {
    import quotes.reflect.*

    val interface = TypeRepr.of[T].typeSymbol
    if (!interface.flags.is(Flags.Trait)) report.errorAndAbort(s"ABI interface must be a trait: ${interface.fullName}")

    val methods = publicMethods(interface)
    rejectOverloads(methods)

    val expressions = methods.map { method =>
      if (!method.name.matches("[A-Za-z_][A-Za-z0-9_]*"))
        abortAt(method, s"ABI method name must be its exact C identifier: ${method.name}")

      val implementation = implementationExpression(method)
      '{ ABIMethod(${ Expr(method.name) }, $implementation) }
    }
    '{ Vector(${ Varargs(expressions) }*) }
  }

  private def implementationExpression(using
      quotes: Quotes
  )(
      method: quotes.reflect.Symbol
  ): Expr[MethodImplementation] =
    if (method.flags.is(quotes.reflect.Flags.Deferred)) '{ MethodImplementation.Remote }
    else
      nativeDelegate(method).fold[Expr[MethodImplementation]]('{ MethodImplementation.Unsupported }) { target =>
        '{ MethodImplementation.NativeDelegate(${ Expr(target) }) }
      }

  private def publicMethods(using quotes: Quotes)(interface: quotes.reflect.Symbol): Vector[quotes.reflect.Symbol] = {
    import quotes.reflect.*
    interface.declaredMethods
      .filterNot(method => method.flags.is(Flags.Synthetic) || method.flags.is(Flags.Private))
      .filterNot(_.name == "<init>")
      .sortBy(_.name)
      .toVector
  }

  private def rejectOverloads(using quotes: Quotes)(methods: Vector[quotes.reflect.Symbol]): Unit = {
    import quotes.reflect.*
    val duplicateNames = methods.groupBy(_.name).collect { case (name, entries) if entries.size > 1 => name }.toVector
    if (duplicateNames.nonEmpty)
      report.errorAndAbort(s"ABI methods may not be overloaded: ${duplicateNames.sorted.mkString(", ")}")
  }

  private def nativeDelegate(using quotes: Quotes)(method: quotes.reflect.Symbol): Option[String] =
    annotationString(method, "net.kurobako.gemstone.abi.NativeDelegate", "NativeDelegate").map { target =>
      if (!target.matches("[A-Za-z_][A-Za-z0-9_]*"))
        abortAt(method, s"native delegate target is not a C identifier: $target")
      target
    }

  private def annotationString(using
      quotes: Quotes
  )(
      method: quotes.reflect.Symbol,
      annotationName: String,
      displayName: String
  ): Option[String] = {
    import quotes.reflect.*
    method.annotations.filter(_.tpe.typeSymbol.fullName == annotationName) match {
      case Nil                                                  => None
      case List(Apply(_, List(Literal(StringConstant(value))))) => Some(value)
      case List(_) => abortAt(method, s"$displayName for ${method.name} requires one literal-string argument")
      case _       => abortAt(method, s"ABI method ${method.name} has multiple $displayName annotations")
    }
  }

  private def abortAt(using quotes: Quotes)(method: quotes.reflect.Symbol, message: String): Nothing = {
    import quotes.reflect.*
    method.pos match {
      case Some(position) => report.errorAndAbort(message, position)
      case None           => report.errorAndAbort(message)
    }
  }
}
