package nichenke.cli

import java.io.File

import org.apache.logging.log4j.scala.{Logging, LoggingContext}
import scopt.OptionParser


case class Config(verbose: Boolean = false,
                  debug: Boolean = false,
                  files: Seq[File] = Seq())

object main extends App with Logging {
  val parser = new OptionParser[Config]("json1") {
    override def showUsageOnError = true

    // TODO: can we get the program name and version from somewhere nicer ?
    head("json1", "1.x")

    // TODO: added debug and verbose from docs, wire into something interesting
    opt[Unit]("debug")
      .hidden()
      .action((_, c) => c.copy(debug = true))
      .text("this option is hidden in the usage text")

    opt[Unit]("verbose")
      .action((_, c) => c.copy(verbose = true))
      .text("verbose is a flag")

    arg[File]("<file>...")
      .unbounded()
      .action((x, c) => c.copy(files = c.files :+ x))
      .text("JSON files to process")
  }

  logger.debug("parsing config options")
  parser.parse(args, Config()) match {
    case Some(config) =>
      config.files.map { fname =>
        LoggingContext += ("some" -> "value")
        logger.info(s"processing file: ${fname}")
      }
    case None =>
      // arguments are bad, error message will have been displayed
  }

}
