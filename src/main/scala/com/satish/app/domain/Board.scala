package com.satish.app.domain

/**
 * Created by satish on 10/06/17.
 * @author satish
 */

/**
 * Board represents the Tic Tac Toe board.
 */
case class Board(state: Map[Cell, Piece]):
  /**
   * Return true if board is empty.
   */
  def isEmpty: Boolean = state.isEmpty

  /**
   * Return true if board is full.
   */
  def isFull: Boolean = state.size == 9

  /**
   * Returns all the empty cells in the board.
   */
  def emptyCells: List[Cell] = Cell.all.filterNot(state.contains)

  /**
   * Return true if board is empty at given cell.
   */
  def isEmptyAt(position: Cell): Boolean = !state.contains(position)

  /**
   * Return true if board is full at given cell.
   */
  def isFullAt(position: Cell): Boolean = state.contains(position)

  /**
   * Places given piece at given cell and returns new board.
   * @param position Cell
   * @param piece Piece to be placed at given position.
   * @return New board with updated state.
   */
  def placePiece(position: Cell, piece: Piece): Board =
     Board(state + (position -> piece))

  def pieceAt(position: Cell) : Option[Piece] = state.get(position)

  def prettyPrint: String =
    val cells: List[Cell] = Cell.all
    val cellValues: List[String] = cells.map(c => pieceAt(c).map({
      case Piece.O => "O"
      case Piece.X => "X"
    }).getOrElse(c.rank.toString))

    val rows : List[List[String]] = cellValues.grouped(3).toList

    val rowValues : List[String] = rows.map(ls => ls.mkString("║ ", " ║ ", " ║"))

    rowValues.mkString("════╬═══╬═══\n","\n════╬═══╬═══\n","\n════╬═══╬═══\n")

object Board:
  def empty: Board = new Board(Map.empty)


object BoardApp extends App:
  val b = Board.empty
  val one = Cell(1).get
  val four = Cell(4).get
  val b1 = b.placePiece(one, Piece.O)
  val b2 = b1.placePiece(four, Piece.X)

  println(b2.prettyPrint)
