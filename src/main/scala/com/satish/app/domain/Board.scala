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

object Board:
  def empty: Board = new Board(Map.empty)


