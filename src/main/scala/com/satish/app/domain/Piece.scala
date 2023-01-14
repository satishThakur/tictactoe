package com.satish.app.domain

enum Piece:
  case O extends Piece
  case X extends Piece

object Piece:
  def apply(raw: String): Option[Piece] =
    raw.trim.toLowerCase match
      case "o" => Some(O)
      case "x" => Some(X)
      case _   => None
