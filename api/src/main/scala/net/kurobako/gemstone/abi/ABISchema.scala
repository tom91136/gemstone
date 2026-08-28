package net.kurobako.gemstone.abi

import net.kurobako.gemstone.Col

enum MethodImplementation {
  case Remote
  case NativeDelegate(target: String)
  case Unsupported
}

final case class ABIMethod(name: String, implementation: MethodImplementation)

final case class CFunction(name: String, functionType: String)

final case class ABIDeclaration(
    name: String,
    functionType: String,
    parameterNames: Col[String],
    implementation: MethodImplementation
)
