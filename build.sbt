/* basic project info */
name := "ficus"

description := "A Scala-friendly wrapper companion for Typesafe config"

startYear := Some(2013)


/* scala versions and options */
scalaVersion := "2.11.8"

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

scalacOptions ++= Seq(
  "-target:jvm-1.8"
)

javacOptions ++= Seq(
  "-Xlint:unchecked", "-Xlint:deprecation"
)

/* dependencies */
libraryDependencies ++= Seq(
  "org.specs2"     %% "specs2-core"       % "3.7.2"  % "test",
  "org.specs2"     %% "specs2-scalacheck" % "3.7.2"  % "test",
  "org.scalacheck" %% "scalacheck"        % "1.13.0" % "test",
  "com.chuusai"    %% "shapeless"         % "2.3.0"  % "test",
  "com.typesafe"   %  "config"            % "1.3.0",
  "org.scala-lang" %  "scala-reflect"     % scalaVersion.value % "provided"
)

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

mappings in (Compile, packageBin) := {
  val ms = mappings.in(Compile, packageBin).value
  ms filter { case (_, toPath) =>
    toPath != "application.conf"
  }
}

Publish.settings


