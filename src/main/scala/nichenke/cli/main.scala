package nichenke.cli

import java.io.File
import scopt.OptionParser


case class Config(verbose: Boolean = false,
                  debug: Boolean = false,
                  files: Seq[File] = Seq())

object main extends App {
  val parser = new OptionParser[Config]("json1") {
    override def showUsageOnError = true

    // TODO: can we get the program name and version from somewhere nicer ?
    head("json1", "1.x")

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

  parser.parse(args, Config()) match {
    case Some(config) =>
      config.files.foreach(println)
    case None =>
      // arguments are bad, error message will have been displayed
  }

}
