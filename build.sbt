import Dependencies._

val scala3Version = "3.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "tictactoe",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    scalacOptions ++= List("-feature", "-deprecation", "-Ykind-projector:underscores", "-source:future"),

    libraryDependencies ++= Seq(
      Libraries.cats,
      Libraries.catsEffect,
      //Libraries.catsEffectTesting,
      Libraries.munit,
      Libraries.munitScalaCheck,
      Libraries.weaver,
      Libraries.weaverScalaCheck,
    ),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  )

ThisBuild / assemblyMergeStrategy := {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

