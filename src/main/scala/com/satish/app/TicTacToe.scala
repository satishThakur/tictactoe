package com.satish.app
import cats.effect.{IO, IOApp}
import com.satish.app.domain.{Board, Cell, Game, Piece, Player, Status}
import cats.effect.std.Random


case class GameSetup(humanPiece : Piece, tossWinner : Player)

object TicTacToe extends IOApp.Simple:
  def run: IO[Unit] =
    IO.println("Hello World!")
    //getUserPiecePreference.as(IO.pure(()))
    for{
      _ <- IO.println("game setup start..")
      game <- gameSetup
      _ <- IO.println("Setup done -> starting game..")
      status <- gameLoop(game)
      _ <- IO.println(s"Game over - ${status}")
    }yield ()


  def gameSetup: IO[Game] =
    for{
      piece <- getUserPiecePreference
      human = Player(piece, false)
      computer = if piece == Piece.X then Player(Piece.O, true) else Player(Piece.X, true)
      _ <- IO.println("toss start..")
      tossWinner <- toss(human, computer)
      _ <- IO.println("toss done..")
    }yield Game(Board.empty, tossWinner, (computer, human))



  def getUserPiecePreference: IO[Piece] =
    for{
      _ <- IO.println("Enter X or O to choose your piece")
      str <- IO.readLine
      p = IO.fromOption(Piece(str))(new RuntimeException)
      piece <- p.orElse(getUserPiecePreference)
    } yield piece

  def toss(p1: Player, p2 : Player): IO[Player] =
    (for{
      r <- Random.scalaUtilRandom[IO]
      b <- r.nextBoolean
    } yield if b then p1 else p2).
      flatTap(p => IO.println(s"Toss won by ${p}, press ENTER to continue"))



  def gameLoop(game: Game): IO[Status] =
    game.status match {
      case Status.Ongoing => IO.println(s"game status ${game.status}") *> IO.println(game.board.prettyPrint) *>
        userInput(game.current, game.board.isEmptyAt).
        map(c => game.move(c)).flatMap(gameLoop(_))
      case other => IO.println(s"game status ${game.status}") *> IO.pure(other)
    }

  def userInput(p : Player, predicate: Cell => Boolean) : IO[Cell] =
    for {
      raw <- IO.println("Enter the cell number") *> IO.readLine
      c = IO.fromOption(Cell.fromString(raw))(new RuntimeException)
      cell <- c.orElse(IO.println("wrong format") *> userInput(p, predicate))
      vcell <- IO.pure(predicate(cell)).flatMap(if _ then IO.pure(cell) else IO.println("cell accupied") *> userInput(p, predicate))
    }yield vcell

  def gameResult(result: Status) : IO[String] = ???


