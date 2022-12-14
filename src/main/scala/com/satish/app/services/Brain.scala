package com.satish.app.services
import com.satish.app.domain.{Board, Cell, Player}

case class Row(empty: List[Cell], mine: List[Cell], opponent: List[Cell])

object Row:
  def apply:Row =  Row(Nil, Nil, Nil)

object Brain:

  def getNextMove(b: Board): Option[Cell] =
    Cell.winnerCombination.foldRight(None: Option[Cell])((w,o) => {
      o.orElse {
        val row = processRow(w, _ => None)
        myWinningMove(row).orElse(opponentWinningmove(row))
      }
    })

  def processRow(cells: List[Cell], query: Cell => Option[Player]): Row =
    cells.foldRight(Row.apply)((c, r) => query(c) match {
      case Some(Player(_, true)) => r.copy(mine = c :: r.mine)
      case Some(Player(_, false)) => r.copy(opponent = c :: r.opponent)
      case _ => r.copy(empty = c :: r.empty)
    })

  def myWinningMove(r : Row): Option[Cell] =
    if r.empty.size == 1 && r.mine.size == 2
    then Some(r.empty(0)) else None

  def opponentWinningmove(r : Row): Option[Cell] =
    if r.empty.size == 1 && r.opponent.size == 2
    then Some(r.empty(0)) else None
