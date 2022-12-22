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

  test("All winner combinations are correct"){
    def isWinner(cells : List[Cell]): Boolean =
      cells.size == 3 &&
        (
          cells.map(_.row).forall(_ == cells.head.row) ||
          cells.map(_.col).forall(_ == cells.head.col) ||
          cells.map(c => (c.row, c.col)).forall{ case (r, c) => r == c } ||
          cells.map(c => (c.row, c.col)).forall{ case (r, c) => r + c == 2 }
          )
    Cell.winnerCombination.foreach{ cells =>
      assert(isWinner(cells), s"Cells $cells are not a winner combination")
    }
  }
  
}
