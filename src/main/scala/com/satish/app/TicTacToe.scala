package com.satish.app
import cats.effect.{IO, IOApp}
import com.satish.app.domain.{Cell, Game, Piece, Player, Status}

import javax.xml.transform.Result

case class GameSetup(humanPiece : Piece, tossWinner : Player)

object TicTacToe extends IOApp.Simple:
  def run: IO[Unit] =
    IO.println("Hello World!")
    getUserPiecePreference.as(IO.pure(()))


  def gameSetup: IO[GameSetup] =
    //ask the human for his choice of Piece.

    ???

  def getUserPiecePreference: IO[Piece] =
    for{
      _ <- IO.println("Enter X or O to choose your piece")
      str <- IO.readLine
      p = IO.fromOption(Piece(str))(new RuntimeException)
      piece <- p.orElse(getUserPiecePreference)
    } yield piece


  def gameLoop(game: Game): IO[Status] = ???

  def userInput(p : Player) : IO[Cell] = ???

  def gameResult(result: Result) : IO[String] = ???


