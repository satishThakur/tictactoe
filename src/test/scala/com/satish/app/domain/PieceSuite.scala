package com.satish.app.domain

class PieceSuite extends munit.FunSuite:
  test("I should only be able to create Piece with x or o"){

    val piece = Piece("x")
    assertEquals(piece.get, Piece.X)

    val piece1 = Piece("x")
    assertEquals(piece1.get, Piece.X)

    val piece2 = Piece("o")
    assertEquals(piece2.get, Piece.O)

    val piece3 = Piece("O")
    assertEquals(piece3.get, Piece.O)

    val piece4 = Piece("z")
    assert(piece4.isEmpty)
  }
