package com.satish.app
import cats.Monad
import cats.effect.Sync
import cats.effect.{IO, IOApp}
import com.satish.app.domain.{Board, Cell, Game, Piece, Player, Status}
import cats.effect.std.Random
import com.satish.app.services.Brain
import com.satish.app.effects.Console
import cats.syntax.all.*

object TicTacToe extends IOApp.Simple:

  def run: IO[Unit] =
    IO.println("Hello World!")
    val console = Console.make[IO]
    runGameAndPrintStatus[IO](console)

  private def runGameAndPrintStatus[F[_]: Sync](console: Console[F]): F[Unit] = for{
    game <- runGameToCompletion(console)
    _ <- printGameStatus(game)(console)
  }yield ()

  private def runGameToCompletion[F[_]: Sync](console: Console[F]): F[Game] =
    for{
      _ <- console.println("game setup start..")
      game <- gameSetup(console)
      _ <- console.println("Setup done -> starting game..")
      game <- gameLoop(game)(console)
    }yield game

  private def printGameStatus[F[_]: Sync](game: Game)(console: Console[F]): F[Unit] =
    for{
      _ <- console.println("Final Board...")
      _ <- console.println(game.board.prettyPrint)
      mess <- gameResult(game.status)
      _ <- console.println(mess)
    }yield ()

  private def gameSetup[F[_]: Sync](console: Console[F]): F[Game] = for{
    piece <- getUserPiecePreference(console)
    human = Player(piece, false)
    computer = if piece == Piece.X then Player(Piece.O, true) else Player(Piece.X, true)
    _ <- console.println("toss start..")
    tossWinner <- toss(human, computer)(console)
    _ <- console.println("toss done..")
  }yield Game(Board.empty, tossWinner, (computer, human))

  private def getUserPiecePreference[F[_]: Monad](console: Console[F]): F[Piece] =
    for{
      _ <- console.println("Enter X or O to choose your piece")
      str <- console.readLine
      //p = IO.fromOption(Piece(str))(new RuntimeException)
      p = Monad[F].pure(Piece(str))
      //piece <- p.orElse(getUserPiecePreference(console))
      piece <- p.flatMap(_.fold(getUserPiecePreference(console))(Monad[F].pure))
    } yield piece

  private def toss[F[_]: Sync](p1: Player, p2 : Player)(console: Console[F]): F[Player] =
    for{
      rand <- Random.scalaUtilRandom[F]
      r <- rand.nextBoolean
      p <- if r then p1.pure[F] else p2.pure[F]
      _ <- console.println(s"Toss won by ${p}, press ENTER to continue")
    } yield p


  private def gameLoop[F[_]: Sync](game: Game)(console: Console[F]): F[Game] =
    game.status match {
      case Status.Ongoing => console.println(s"game status ${game.status}") *> console.println(game.board.prettyPrint) *>
        getPlayerInput(game.board, game.allPlayers,game.current, game.board.emptyCells)(console).
        map(c => game.move(c)).flatMap(gameLoop[F](_)(console))
      case _ => console.println(s"game status ${game.status}") *> Monad[F].pure(game)
    }

  private def getPlayerInput[F[_]: Sync](b: Board, players: List[Player], current: Player, emptyCells : List[Cell])(console: Console[F]): F[Cell] =
    current match {
      case Player(_, true) => computerInput(b, players, emptyCells)(console)
      case _ => userInput(current, c => emptyCells.contains(c))(console)
    }


  private def userInput[F[_]: Monad](p: Player, predicate: Cell => Boolean)(console: Console[F]): F[Cell] =
    for {
      raw <- console.println("Enter the cell number") *> console.readLine.map(Cell.fromString(_))
      //c = Monad[F].pure(Cell.fromString(raw))
      cell <- raw.fold(console.println("wrong format") *> userInput(p, predicate)(console))(Monad[F].pure)
      //cell <- c.flatMap(_.fold(console.println("wrong format") *> userInput(p, predicate)(console), Monad[F].pure(_)))
      //cell <- c.orElse(console.println("wrong format") *> userInput(p, predicate)(console))
      vcell <- Monad[F].pure(predicate(cell)).flatMap(if _ then Monad[F].pure(cell) else console.println("cell accupied") *> userInput(p, predicate)(console))
    }yield vcell

  //TODO : can monadtransformer be used here?
  private def computerInput[F[_]: Sync](b: Board, players: List[Player], emptyCells: List[Cell])(console: Console[F]) : F[Cell] =
    Monad[F].pure(Brain.getNextMove(b, players)).flatMap{
      case Some(c) => Monad[F].pure(c)
      case None =>
        for {
          r <- Random.scalaUtilRandom[F]
          b <- r.nextIntBounded(emptyCells.size)
        }yield emptyCells(b)
    }.flatTap(_ => console.println("Computer turn: Press ENTER to continue")) <* console.readLine


  private def gameResult[F[_]: Monad](status: Status) : F[String] = status match {
    case Status.Draw => Monad[F].pure("Game ended in draw, better luck next time!!")
    case Status.Completed(Player(_, true)) => Monad[F].pure("Computer won, you lost")
    case _ => Monad[F].pure("Yay!! You won!!")
  }



