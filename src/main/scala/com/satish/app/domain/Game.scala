package com.satish.app.domain
import com.satish.app.state.*


case class Game(board: Board, current: Player, players: (Player, Player)):
  def allPlayers: List[Player] = List(players(0), players(1))

  def move(cell: Cell) : Game =
    val stf = Game.transision(cell, current.piece)
    stf.run(this)(1)

  def next: Player = if current == players(0) then players(1) else players(0)

  def status: Status =
    val w = winner
    if w.isDefined then Status.Completed(w.get)
    else if board.isFull then Status.Draw
    else Status.Ongoing

  def winner: Option[Player] =
    val mayBeWinner = next
    val comb: Option[List[Cell]] = Cell.winnerCombination.find(p => p.forall(c => board.isPieceAt(c, next.piece)))
    comb.map(_ => next)


object Game:
  def apply(board: Board, current: Player, players: (Player, Player)): Game =
    new Game(board, current, players)

  def singleMove(cell: Cell, piece: Piece)(g: Game): Game =
    val newBoard = g.board.placePiece(cell, piece)
    val nextPlayer = if g.current == g.players(0) then g.players(1) else g.players(0)
    Game(newBoard, nextPlayer, g.players)

  def transision(cell: Cell, piece: Piece): State[Game, Status] = for{
    _ <- State.modify[Game](singleMove(cell, piece))
    s <- State.get[Game]
  }yield s.status


