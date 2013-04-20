/* basic project info */
name := "ficus"

organization := "ceedubs"

version := "0.1.0-SNAPSHOT"

description := "A Scala-friendly wrapper companion for Typesafe config"

homepage := Some(url("https://github.com/ceedubs/ficus"))

startYear := Some(2013)

licenses := Seq(
  ("GPLv3", url("http://www.gnu.org/licenses/gpl-3.0.txt"))
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/ceedubs/ficus"),
    "scm:git:https://github.com/ceedubs/ficus.git",
    Some("scm:git:git@github.com:ceedubs/ficus.git")
  )
)

/* scala versions and options */
scalaVersion := "2.10.1"

crossScalaVersions := Seq(
  "2.8.0", "2.8.1", "2.8.2",
  "2.9.0", "2.9.0-1",
  "2.9.1", "2.9.1-1",
  "2.9.2",
  "2.9.3"
)

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8"
  // "-optimise"   // this option will slow your build
)

scalacOptions ++= Seq(
  "-Yclosure-elim",
  "-Yinline"
)

// These language flags will be used only for 2.10.x.
// Uncomment those you need, or if you hate SIP-18, all of them.
scalacOptions <++= scalaVersion map { sv =>
  if (sv startsWith "2.10") List(
    "-Xverify",
    "-Ywarn-all",
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:higherKinds"
  )
  else Nil
}

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

/* entry point */
mainClass in (Compile, packageBin) := Some("ceedubs.config.Main")

mainClass in (Compile, run) := Some("ceedubs.config.Main")

/* dependencies */
libraryDependencies ++= Seq (
  "org.specs2"   %% "specs2" % "1.15-SNAPSHOT" % "test",
  "com.typesafe" %  "config" % "1.0.0"
)

/* you may need these repos */
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)

/* testing */
parallelExecution in Test := false

/* sbt behavior */
logLevel in compile := Level.Warn

traceLevel := 5

offline := false

/* publishing */
publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some(
    "snapshots" at nexus + "content/repositories/snapshots"
  )
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

mappings in (Compile, packageBin) ~= { (ms: Seq[(File, String)]) =>
  ms filter { case (file, toPath) =>
      toPath != "application.conf"
  }
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <developers>
    <developer>
      <id>ceedubs</id>
      <name>Cody Allen</name>
      <email>ceedubs@gmail.com</email>
    </developer>
  </developers>
)