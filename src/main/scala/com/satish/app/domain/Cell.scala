package com.satish.app.domain

/**
 * Cell ia a subset of Int but restricts the values to 1 to 9.
 * Cell represent a cell in the TicTacToe board.
 */

opaque type Cell <: Int = Int

object Cell:

  /**
   * Creates a Cell from an Int.
   * @param p the Int to be converted to Cell
   * @return a Cell if i is between 1 and 9, otherwise None
   */
  def apply(p: Int): Option[Cell] =
    if p >= 1 && p <= 9 then Some(p) else None

  /**
   * All the possible Cells
   */
  def all: List[Cell] = (1 to 9).toList.flatMap(apply)

  /**
   * Converts a raw string to a Cell.
   */
  def fromString(s: String): Option[Cell] = apply(s.trim.toInt)

  /**
   *
   * Returns all the winner combinations of the Cells.
   */
  def winnerCombination: List[List[Cell]] = List(
    List(1, 2, 3),
    List(4, 5, 6),
    List(7, 8, 9),
    List(1, 4, 7),
    List(2, 5, 8),
    List(3, 6, 9),
    List(1, 5, 9),
    List(3, 5, 7)
  )

  extension (c: Cell)
    def rank: Int = c
    def row: Int = c  / 3
    def col: Int = c  % 3

object CellApp extends App:
  println(Cell.all)
  Cell.all.foreach(c => println(s"Cell: $c, Row: ${c.row}, Col: ${c.col}"))
  println(Cell.fromString("1"))
  println(Cell.fromString("10"))

