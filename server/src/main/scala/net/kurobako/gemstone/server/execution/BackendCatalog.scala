package net.kurobako.gemstone.server.execution

final class BackendCatalog private (private val entries: Map[ProgramEncoding, RuntimeBackend]) {
  def backend(encoding: ProgramEncoding): Either[RuntimeError, RuntimeBackend] =
    entries.get(encoding).toRight(RuntimeError.UnknownEncoding(encoding))
}

object BackendCatalog {
  def from(backends: RuntimeBackend*): BackendCatalog =
    BackendCatalog(backends.view.map(backend => backend.encoding -> backend).toMap)
}
