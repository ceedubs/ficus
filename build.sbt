/* basic project info */
name := "ficus"

organization := "net.ceedubs"

description := "A Scala-friendly wrapper companion for Typesafe config"

homepage := Some(url("https://github.com/ceedubs/ficus"))

startYear := Some(2013)

licenses := Seq(
  "MIT License" -> url("http://www.opensource.org/licenses/mit-license.html")
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/ceedubs/ficus"),
    "scm:git:https://github.com/ceedubs/ficus.git",
    Some("scm:git:git@github.com:ceedubs/ficus.git")
  )
)

/* scala versions and options */
scalaVersion := "2.11.0"

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8"
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

/* dependencies */
libraryDependencies <++= scalaVersion { sv =>
  Seq(
    "org.specs2"       %% "specs2"        % "2.3.11"   % "test",
    "org.scalacheck"   %% "scalacheck"    % "1.11.3"   % "test",
    "com.chuusai"      %% "shapeless"     % "2.0.0"    % "test",
    "com.typesafe"     %  "config"        % "1.2.1",
    "com.google.guava" %  "guava"         % "18.0",
    "org.scala-lang"   %  "scala-reflect" % sv         % "provided")
}

/* you may need these repos */
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)

/* testing */
parallelExecution in Test := true

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
