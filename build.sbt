import scala.collection.Seq

ThisBuild / publishAsOSSProject := true

homepage in ThisBuild := Some(url("https://github.com/slamdata/async-blobstore"))

scmInfo in ThisBuild := Some(ScmInfo(
  url("https://github.com/slamdata/async-blobstore"),
  "scm:git@github.com:slamdata/async-blobstore.git"))

val ArgonautVersion = "6.2.3"
val AwsSdkVersion = "2.9.1"
val AwsV1SdkVersion = "1.11.634"
val Fs2Version = "1.0.5"
val MonixVersion = "3.0.0"

// Include to also publish a project's tests
lazy val publishTestsSettings = Seq(
  publishArtifact in (Test, packageBin) := true)

lazy val root = project
  .in(file("."))
  .settings(noPublishSettings)
  .aggregate(core, azure, s3)
  .enablePlugins(AutomateHeaderPlugin)

lazy val core = project
  .in(file("core"))
  .settings(addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"))
  .settings(
    name := "async-blobstore-core",
    libraryDependencies ++= Seq(
      "com.github.julien-truffaut" %% "monocle-core" % "1.6.0",
      "co.fs2" %% "fs2-core" % "1.0.5",
      "co.fs2" %% "fs2-reactive-streams" % "1.0.5"))
  .enablePlugins(AutomateHeaderPlugin)

lazy val s3 = project
  .in(file("s3"))
  .dependsOn(core)
  .settings(addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"))
  .settings(
    name := "async-blobstore-s3",
    libraryDependencies ++= Seq(
      "io.argonaut"  %% "argonaut" % ArgonautVersion,
      "co.fs2" %% "fs2-core" % Fs2Version,
      "io.monix" %% "monix-catnap" % MonixVersion,
      "software.amazon.awssdk" % "netty-nio-client" % AwsSdkVersion,
      // We depend on both v1 and v2 S3 SDKs because of this ticket:
      // https://github.com/aws/aws-sdk-java-v2/issues/272
      // Depending on both is the recommended workaround
      "software.amazon.awssdk" % "s3" % AwsSdkVersion,
      "com.amazonaws" % "aws-java-sdk-s3" % AwsV1SdkVersion))
  .enablePlugins(AutomateHeaderPlugin)

lazy val azure = project
  .in(file("azure"))
  .dependsOn(core)
  .settings(addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"))
  .settings(
    name := "async-blobstore-azure",
    libraryDependencies ++= Seq(
      "org.slf4s" %% "slf4s-api" % "1.7.25",
      "com.microsoft.azure" % "azure-storage-blob" % "10.5.0",
      "com.azure" % "azure-identity" % "1.0.0",
      "eu.timepit" %% "refined" % "0.9.9",
      // netty-all isn't strictly necessary but takes advantage of native libs.
      // Azure doesn't pull in libs like netty-transport-native-kqueue,
      // netty-transport-native-unix-common and netty-transport-native-epoll.
      // Keep nettyVersion in sync with the version that Azure pulls in.
      "io.reactivex.rxjava2" % "rxjava" % "2.2.2"))
  .enablePlugins(AutomateHeaderPlugin)
