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
      p <- console.readLine.map(Piece(_))
      piece <- p.fold(getUserPiecePreference(console))(_.pure)
    } yield piece

  private def toss[F[_]: Sync](p1: Player, p2 : Player)(console: Console[F]): F[Player] =
    for{
      rand <- Random.scalaUtilRandom[F]
      p <- rand.nextBoolean.map(if _ then p1 else p2)
      _ <- console.println(s"Toss won by ${p}, press ENTER to continue")
    } yield p

  private def gameLoop[F[_]: Sync](game: Game)(console: Console[F]): F[Game] =
    game.status match {
      case Status.Ongoing => for{
        _ <- console.println(s"game status ${game.status}")
        _ <- console.println(game.board.prettyPrint)
        c <- getPlayerInput(game.board, game.allPlayers,game.current, game.board.emptyCells)(console)
        newGame <- game.move(c).pure
        g <- gameLoop(newGame)(console)
      }yield g
      case _ => console.println(s"game status ${game.status}") *> game.pure
    }

  private def getPlayerInput[F[_]: Sync](b: Board, players: List[Player], current: Player, emptyCells : List[Cell])(console: Console[F]): F[Cell] =
    current match {
      case Player(_, true) => computerInput(b, players, emptyCells)(console)
      case _ => userInput(current, c => emptyCells.contains(c))(console)
    }


  private def userInput[F[_]: Monad](p: Player, predicate: Cell => Boolean)(console: Console[F]): F[Cell] =
    for {
      raw <- console.println("Enter the cell number") *> console.readLine.map(Cell.fromString)
      cell <- raw.fold(console.println("wrong format") *> userInput(p, predicate)(console))(_.pure)
      vcell <- predicate(cell).pure.flatMap(if _ then cell.pure else console.println("cell occupied") *> userInput(p, predicate)(console))
    }yield vcell

  //TODO : can monadtransformer be used here?
  private def computerInput[F[_]: Sync](b: Board, players: List[Player], emptyCells: List[Cell])(console: Console[F]) : F[Cell] =
    Monad[F].pure(Brain.getNextMove(b, players)).flatMap{
      case Some(c) => c.pure
      case None =>
        for {
          r <- Random.scalaUtilRandom[F]
          b <- r.nextIntBounded(emptyCells.size)
        }yield emptyCells(b)
    }.flatTap(_ => console.println("Computer turn: Press ENTER to continue")) <* console.readLine


  private def gameResult[F[_]: Monad](status: Status) : F[String] = status match {
    case Status.Draw => "Game ended in draw, better luck next time!!".pure
    case Status.Completed(Player(_, true)) => "Computer won, you lost".pure
    case _ => "Yay!! You won!!".pure
  }



