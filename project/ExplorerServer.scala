import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import scala.util.Failure
import scala.util.Success
import scala.util.Try

/** Serves a directory of static files over HTTP, so the built explorer can be opened in a browser while it is worked
  * on. It uses the JDK's own server, which keeps the build free of another plugin, and runs on daemon threads so an
  * interactive sbt session keeps serving until it exits.
  */
object ExplorerServer {

  /** Serves `root` on `port`; the address it bound, or why it could not. */
  def start(root: Path, port: Int): Either[String, InetSocketAddress] =
    Try(HttpServer.create(new InetSocketAddress(port), 0)) match {
      case Failure(error)  => Left(s"cannot listen on port $port: ${error.getMessage}")
      case Success(server) =>
        val base = root.toAbsolutePath.normalize
        server.createContext("/", (exchange: HttpExchange) => serve(base, exchange))
        server.setExecutor(Executors.newCachedThreadPool { (runnable: Runnable) =>
          val thread = new Thread(runnable, "explorer-server")
          thread.setDaemon(true)
          thread
        })
        server.start()
        Right(server.getAddress)
    }

  /** Holds the calling thread until the build is interrupted, so a task started from sbt keeps the server up. */
  def await(): Unit =
    try new CountDownLatch(1).await()
    catch { case _: InterruptedException => () }

  /** Answers one request from the browser, with `base` as the directory's root. */
  private def serve(base: Path, exchange: HttpExchange): Unit =
    try {
      val method = exchange.getRequestMethod
      if (method != "GET" && method != "HEAD") exchange.sendResponseHeaders(405, -1)
      else {
        val requested = exchange.getRequestURI.getPath
        val relative  =
          if (requested == null || requested.isEmpty || requested == "/") "index.html" else requested.stripPrefix("/")
        val file = base.resolve(relative).normalize
        if (!file.startsWith(base)) respond(exchange, 403, s"$requested is outside the explorer")
        else if (!Files.isRegularFile(file)) respond(exchange, 404, s"$requested is not in the explorer")
        else {
          exchange.getResponseHeaders.set("Content-Type", contentType(file))
          // the site is rebuilt in place while it is served, so a browser must not reuse an older script
          exchange.getResponseHeaders.set("Cache-Control", "no-store")
          if (method == "HEAD") exchange.sendResponseHeaders(200, -1)
          else {
            exchange.sendResponseHeaders(200, Files.size(file))
            val body = exchange.getResponseBody
            try { val _ = Files.copy(file, body) }
            finally body.close()
          }
        }
      }
    } catch {
      // the browser navigated away or refreshed in the middle of a response, which is not the server's concern
      case _: IOException => ()
    } finally exchange.close()

  private def respond(exchange: HttpExchange, status: Int, message: String): Unit = {
    val body = s"$message\n".getBytes(StandardCharsets.UTF_8)
    exchange.getResponseHeaders.set("Content-Type", "text/plain; charset=utf-8")
    exchange.sendResponseHeaders(status, body.length.toLong)
    val out = exchange.getResponseBody
    try out.write(body)
    finally out.close()
  }

  /** The type a browser needs to load `file` natively. */
  private def contentType(file: Path): String = {
    val name      = file.getFileName.toString
    val dot       = name.lastIndexOf('.')
    val extension = if (dot < 0) "" else name.substring(dot + 1).toLowerCase
    extension match {
      case "html" | "htm" => "text/html; charset=utf-8"
      case "js" | "mjs"   => "text/javascript; charset=utf-8"
      case "css"          => "text/css; charset=utf-8"
      case "json" | "map" => "application/json; charset=utf-8"
      case "wasm"         => "application/wasm"
      case "svg"          => "image/svg+xml"
      case "png"          => "image/png"
      case "jpg" | "jpeg" => "image/jpeg"
      case "ico"          => "image/x-icon"
      case "woff2"        => "font/woff2"
      case _              => "application/octet-stream"
    }
  }
}
