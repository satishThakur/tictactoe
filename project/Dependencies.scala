import sbt._
import sbt.librarymanagement.Configurations.Test

object Dependencies {
  object V {
    val cats             = "2.9.0"
    val catsEffect       = "3.4.4"
    val munit            = "0.7.29"
    val weaver           = "0.8.1"
    val munitScalaCheck  = "0.7.29"
    val weaverScalaCheck = "0.8.1"
  }

  object Libraries {
    val cats            = "org.typelevel"         %% "cats-core"         % V.cats
    val catsEffect       = "org.typelevel"        %% "cats-effect"       % V.catsEffect
    val munit            = "org.scalameta"        %% "munit"             % V.munit             % Test
    val munitScalaCheck  = "org.scalameta"        %% "munit-scalacheck"  % V.munitScalaCheck   % Test
    val weaver           = "com.disneystreaming"  %% "weaver-cats"       % V.weaver            % Test
    val weaverScalaCheck = "com.disneystreaming"  %% "weaver-scalacheck" % V.weaverScalaCheck  % Test

  }
}

