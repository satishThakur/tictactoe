package com.satish.app.services
import com.satish.app.domain.{Board, Cell, Piece, Player}

case class Row(empty: List[Cell], mine: List[Cell], opponent: List[Cell])

object Row:
  def apply: Row =  Row(Nil, Nil, Nil)

object Brain:

  def getNextMove(b: Board, players: List[Player]): Option[Cell] =

    val pieceToPlayer: Map[Piece, Player] = 
      players.groupMapReduce(_.piece)(identity)((p1, _) => p1)
    Cell.winnerCombination.foldRight(None: Option[Cell])((w,o) => {
      o.orElse {
        val row = processRow(w, queryBoard(b, pieceToPlayer))
        myWinningMove(row).orElse(opponentWinningMove(row))
      }
    })

  private def queryBoard(b: Board, pieceToPlayer: Map[Piece, Player])(c: Cell): Option[Player] =
    b.pieceAt(c).flatMap(pieceToPlayer.get)


  private def processRow(cells: List[Cell], query: Cell => Option[Player]): Row =
    cells.foldRight(Row.apply)((c, r) => query(c) match {
      case Some(Player(_, true)) => r.copy(mine = c :: r.mine)
      case Some(Player(_, false)) => r.copy(opponent = c :: r.opponent)
      case _ => r.copy(empty = c :: r.empty)
    })

  private def myWinningMove(r : Row): Option[Cell] =
    if r.empty.size == 1 && r.mine.size == 2
    then Some(r.empty.head) else None

  private def opponentWinningMove(r : Row): Option[Cell] =
    if r.empty.size == 1 && r.opponent.size == 2
    then Some(r.empty.head) else None
