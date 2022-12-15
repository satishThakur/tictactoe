package com.satish.app.domain

class GameSuite extends munit.FunSuite {

  test("status of an empty game should be in progress") {
    val game = Game(Board.empty,Player(Piece.O,true),(Player(Piece.O, true), Player(Piece.X, false)))
    assertEquals(game.status, Status.Ongoing)
  }

}
