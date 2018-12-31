javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

name := "json1"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.+"
libraryDependencies += "com.github.scopt" %% "scopt" % "3.6.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
