// Your profile name of the sonatype account. The default is the same with the organization value
sonatypeProfileName := "com.iheart"

import ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)

pomExtra in Global := {
  <url>https://github.com/iheartradio/ficus/</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:github.com/iheart/ficus</connection>
    <developerConnection>scm:git:git@github.com:iheart/ficus</developerConnection>
    <url>github.com/iheart/ficus</url>
  </scm>
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
}
