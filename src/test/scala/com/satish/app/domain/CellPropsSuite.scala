package com.satish.app.domain
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop.*

class CellPropsSuite extends ScalaCheckSuite:

  property("I should be able to create cell between 1 and 9"){
    forAll(Gen.choose(1,9)){ i => Cell(i).isDefined
    }
  }

  property("I should not be able to create Cell with rank less than 1"){
    forAll{ (i: Int) =>
      (i < 1) ==> (Cell(i).isEmpty)
    }
  }

  property("I should not be able to create Cell with rank greater than 9"){
    forAll{ (i: Int) =>
      (i > 9) ==> (Cell(i).isEmpty)
    }
  }


