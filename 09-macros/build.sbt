scalaVersion := "2.11.6"

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
