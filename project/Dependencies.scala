import sbt._
import sbt.librarymanagement.Configurations.Test

object Dependencies {
  object V {
    val cats             = "2.9.0"
    val catsEffect       = "3.4.1"
    val munit            = "0.7.29"
  }

  object Libraries {
    val cats         = "org.typelevel"  %% "cats-core"      % V.cats
    val catsEffect   = "org.typelevel"  %% "cats-effect"    % V.catsEffect
    val munit        = "org.scalameta"  %% "munit"          % V.munit         % Test
  }
}

