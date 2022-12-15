package com.satish.app.services

import com.satish.app.domain.{Board, Cell, Piece, Player}

class BrainSuite extends munit.FunSuite {

  test("for computer wining combination it picks the cell"){
    val board = Board(
      Cell(1).get -> Piece.X,
      Cell(2).get -> Piece.O,
      Cell(7).get -> Piece.O,
      Cell(5).get -> Piece.X,
      Cell(3).get -> Piece.X)

    val players = List(Player(Piece.O, true), Player(Piece.X, false))

    val cell = Brain.getNextMove(board, players)
    assertEquals(cell, Cell(9))
  }

  test("for opponent wining combination it picks the cell"){
    val board = Board(
      Cell(1).get -> Piece.X,
      Cell(2).get -> Piece.O,
      Cell(4).get -> Piece.O,
      Cell(5).get -> Piece.O,
      Cell(3).get -> Piece.X,
      Cell(9).get -> Piece.O)

    val players = List(Player(Piece.O, true), Player(Piece.X, false))

    val cell = Brain.getNextMove(board, players)
    assertEquals(cell, Cell(8))
  }

  test("in case both can win, computer is selfish - and chooses for it to win"){
    val board = Board(
      Cell(1).get -> Piece.X,
      Cell(2).get -> Piece.O,
      Cell(4).get -> Piece.X,
      Cell(5).get -> Piece.O,
      )

    val players = List(Player(Piece.O, true), Player(Piece.X, false))

    val cell = Brain.getNextMove(board, players)
    assertEquals(cell, Cell(8))
  }

}
