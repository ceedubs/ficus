import sbtrelease.Version

val gc          = TaskKey[Unit]("gc", "runs garbage collector")
lazy val gcTask = gc := {
  println("requesting garbage collection")
  System gc ()
}

ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(List("mimaReportBinaryIssues"), name = Some("Report binary compatibility issues")),
  WorkflowStep.Sbt(List("clean", "coverage", "test"), name = Some("Build project"))
)

ThisBuild / githubWorkflowBuildPostamble ++= Seq(
  // See https://github.com/scoverage/sbt-coveralls#github-actions-integration
  WorkflowStep.Sbt(
    List("coverageReport", "coveralls"),
    name = Some("Upload coverage data to Coveralls"),
    env = Map(
      "COVERALLS_REPO_TOKEN" -> "${{ secrets.GITHUB_TOKEN }}",
      "COVERALLS_FLAG_NAME"  -> "Scala ${{ matrix.scala }}"
    )
  )
)

// This is causing problems with env variables being passed in, see
// https://github.com/sbt/sbt/issues/6468
ThisBuild / githubWorkflowUseSbtThinClient := false

ThisBuild / githubWorkflowPublishTargetBranches := Seq()

ThisBuild / crossScalaVersions := Seq("2.10.7", "2.11.12", "2.13.6", "3.0.1", "2.12.14")
ThisBuild / scalaVersion       := (ThisBuild / crossScalaVersions).value.last

// Coveralls doesn't really work with Scala 2.10.7 so we are disabling it for CI
ThisBuild / githubWorkflowScalaVersions -= "2.10.7"

lazy val root = project
  .in(file("."))
  .settings(
    /* basic project info */
    name                            := "ficus",
    description                     := "A Scala-friendly wrapper companion for Typesafe config",
    startYear                       := Some(2013),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-target:jvm-1." + {
        CrossVersion
          .partialVersion(scalaVersion.value)
          .collect {
            case (2, minor) if minor <= 10 & scalaVersion.value == "2.10.7" => "8"
            case (2, minor) if minor <= 10                                  => "7"
          }
          .getOrElse("8")
      }
    ) ++ (if (scalaVersion.value.startsWith("2.11") || scalaVersion.value.startsWith("2.10"))
            Seq("-Yclosure-elim", "-Yinline")
          else Seq.empty[String]),
    javacOptions ++= Seq(
      "-Xlint:unchecked",
      "-Xlint:deprecation"
    ),
    Compile / unmanagedSourceDirectories ++= {
      (Compile / unmanagedSourceDirectories).value.flatMap { dir =>
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, 13)) => Seq(file(dir.getPath ++ "-2.13+"))
          case Some((2, _))  => Seq(file(dir.getPath ++ "-2.13-"))
          case _             => Nil
        }
      }
    },
    Test / unmanagedSourceDirectories ++= {
      (Test / unmanagedSourceDirectories).value.flatMap { dir =>
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, 13)) => Seq(file(dir.getPath ++ "-2.13+"))
          case Some((2, _))  => Seq(file(dir.getPath ++ "-2.13-"))
          case _             => Nil
        }
      }
    },
    libraryDependencies ++=
      (if (scalaVersion.value.startsWith("2.")) {
         val specs2Version = if (scalaVersion.value.startsWith("2.10")) "3.10.0" else "4.8.3"
         Seq(
           "org.specs2"    %% "specs2-core"       % specs2Version      % Test,
           "org.specs2"    %% "specs2-scalacheck" % specs2Version      % Test,
           "com.chuusai"   %% "shapeless"         % "2.3.3"            % Test,
           "org.scala-lang" % "scala-reflect"     % scalaVersion.value % Provided,
           "org.scala-lang" % "scala-compiler"    % scalaVersion.value % Provided
         )
       } else
         Seq(
           "org.specs2"    %% "specs2-core"         % "5.0.0-ALPHA-03" % Test,
           "org.specs2"    %% "specs2-scalacheck"   % "5.0.0-ALPHA-03" % Test,
           "org.typelevel" %% "shapeless3-typeable" % "3.0.2"          % Test
         )) ++
        Seq(
          "org.scalacheck" %% "scalacheck" % "1.15.4" % Test,
          "com.typesafe"    % "config"     % "1.3.4"
        ) ++
        (if (scalaVersion.value.startsWith("2.") && !scalaVersion.value.startsWith("2.13"))
           Seq(
             compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
             "org.typelevel" %% "macro-compat" % "1.1.1"
           )
         else
           Seq.empty[ModuleID]),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.bintrayRepo("iheartradio", "maven"),
      Resolver.jcenterRepo
    ),
    Test / parallelExecution        := true,
    /* sbt behavior */
    compile / logLevel              := Level.Warn,
    traceLevel                      := 5,
    offline                         := false,
    Compile / packageBin / mappings := {
      val ms = (Compile / packageBin / mappings).value
      ms filter { case (_, toPath) =>
        toPath != "application.conf"
      }
    },
    Publish.settings,
    releaseCrossBuild               := true,
    mimaPreviousArtifacts           :=
      Version(version.value)
        .collect {
          case Version(major, (minor :: bugfix :: _), _) if (scalaBinaryVersion.value != "2.10") && bugfix > 0 =>
            Set(organization.value %% name.value % Seq(major, minor, bugfix - 1).mkString("."))
        }
        .getOrElse(Set.empty),
    gcTask
  )
