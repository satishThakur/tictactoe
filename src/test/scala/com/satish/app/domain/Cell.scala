package com.satish.app.domain

class CellSuite extends munit.FunSuite {

  test("Test Cell can only be created from 1 to 9"){
    1 to 9 foreach { i =>
      val cell = Cell(i)
      assertEquals(cell.get.rank, i)
    }
  }

  test("Test Cell can not be created from outside 1 to 9"){
    val cell = Cell(0)
    assertEquals(cell, None)
    val cell2 = Cell(10)
    assertEquals(cell2, None)
  }
  
}
