import sbt._, Keys._
import bintray.BintrayKeys._


object Publish {

  val bintraySettings = Seq(
    bintrayOrganization := Some("iheartradio"),
    bintrayPackageLabels := Seq("typesafe-config", "parser", "config")
  )

  val publishingSettings = Seq(

    organization in ThisBuild := "com.iheart",
    publishMavenStyle := true,
    licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.html")),
    homepage := Some(url("http://iheartradio.github.io/ficus")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/iheartradio/ficus"),
      "git@github.com:iheartradio/ficus.git",
      Some("git@github.com:iheartradio/ficus.git"))),
    pomIncludeRepository := { _ => false },
    publishArtifact in Test := false,
    pomExtra := (
      <developers>
        <developer>
          <id>ceedubs</id>
          <name>Cody Allen</name>
          <email>ceedubs@gmail.com</email>
        </developer>
        <developer>
          <id>kailuowang</id>
          <name>Kailuo Wang</name>
          <email>kailuo.wang@gmail.com</email>
        </developer>
      </developers>
      )
  )

  val settings = bintraySettings ++ publishingSettings
}
