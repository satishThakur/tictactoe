package com.satish.app.domain
import com.satish.app.state.*


case class Game(board: Board, current: Player, players: (Player, Player)):
  def move(cell: Cell) : Game =
    val stf = Game.transision(cell, current.piece)
    stf.run(this)._2

  def status: Status = ???

object Game:
  def apply(board: Board, current: Player, players: (Player, Player)): Game =
    Game(board, current, players)

  def singleMove(cell: Cell, piece: Piece)(g: Game): Game =
    ???

  def transision(cell: Cell, piece: Piece): State[Game, Status] = for{
    _ <- State.modify[Game](singleMove(cell, piece))
    s <- State.get[Game]
  }yield s.status


