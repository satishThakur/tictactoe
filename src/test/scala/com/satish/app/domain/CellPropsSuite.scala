package com.satish.app.domain
import munit.ScalaCheckSuite
import org.scalacheck.Prop.*

class CellPropsSuite extends ScalaCheckSuite:

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
