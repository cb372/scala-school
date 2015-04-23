scalaVersion := "2.11.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "com.typesafe.play" %% "play-json" % "2.4.0-RC1",
  "io.argonaut" %% "argonaut" % "6.0.4",
  "org.spire-math" %% "jawn-ast" % "0.7.4",
  "com.propensive" %% "rapture-json-jawn" % "1.1.0"
)
