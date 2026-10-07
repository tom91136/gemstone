package net.kurobako.gemstone.testing

import munit.Assertions
import munit.Location

object either {
  extension [E, A](result: Either[E, A]) {
    def value(using Location): A = result match {
      case Right(value) => value
      case Left(error)  => Assertions.fail(s"expected Right, found Left($error)")
    }

    def leftValue(using Location): E = result match {
      case Left(error)  => error
      case Right(value) => Assertions.fail(s"expected Left, found Right($value)")
    }
  }
}
