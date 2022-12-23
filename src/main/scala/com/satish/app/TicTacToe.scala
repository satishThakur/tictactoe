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
    val instance = new TicTacToeCli[IO](console)
    instance.runGameAndPrintStatus
end TicTacToe

class TicTacToeCli[F[_]: Sync](console: Console[F]):
  def runGameAndPrintStatus: F[Unit] = for {
    game <- runGameToCompletion
    _ <- printGameStatus(game)
  } yield ()

  private def runGameToCompletion: F[Game] =
    for {
      _ <- console.println("game setup start..")
      game <- gameSetup
      _ <- console.println("Setup done -> starting game..")
      game <- gameLoop(game)
    } yield game

  private def printGameStatus(game: Game): F[Unit] =
    for {
      _ <- console.println("Final Board...")
      _ <- console.println(game.board.prettyPrint)
      mess <- gameResult(game.status)
      _ <- console.println(mess)
    } yield ()

  private def gameSetup: F[Game] = for {
    piece <- getUserPiecePreference
    human = Player(piece, false)
    computer = if piece == Piece.X then Player(Piece.O, true) else Player(Piece.X, true)
    _ <- console.println("toss start..")
    tossWinner <- toss(human, computer)
    _ <- console.println("toss done..")
  } yield Game(Board.empty, tossWinner, (computer, human))

  private def getUserPiecePreference: F[Piece] =
    for {
      _ <- console.println("Enter X or O to choose your piece")
      p <- console.readLine.map(Piece(_))
      piece <- p.fold(getUserPiecePreference)(_.pure)
    } yield piece

  private def toss(p1: Player, p2: Player): F[Player] =
    for {
      rand <- Random.scalaUtilRandom[F]
      p <- rand.nextBoolean.map(if _ then p1 else p2)
      _ <- console.println(s"Toss won by ${p}, press ENTER to continue")
    } yield p

  private def gameLoop(game: Game): F[Game] =
    game.status match {
      case Status.Ongoing => for {
        _ <- console.println(s"game status ${game.status}")
        _ <- console.println(game.board.prettyPrint)
        c <- getPlayerInput(game.board, game.allPlayers, game.current, game.board.emptyCells)
        newGame <- game.move(c).pure
        g <- gameLoop(newGame)
      } yield g
      case _ => console.println(s"game status ${game.status}") *> game.pure
    }

  private def getPlayerInput(b: Board, players: List[Player], current: Player, emptyCells: List[Cell]): F[Cell] =
    current match {
      case Player(_, true) => computerInput(b, players, emptyCells)
      case _ => userInput(current, c => emptyCells.contains(c))
    }


  private def userInput(p: Player, predicate: Cell => Boolean): F[Cell] =
    for {
      raw <- console.println("Enter the cell number") *> console.readLine.map(Cell.fromString)
      cell <- raw.fold(console.println("wrong format") *> userInput(p, predicate))(_.pure)
      vcell <- predicate(cell).pure.flatMap(if _ then cell.pure else console.println("cell occupied") *> userInput(p, predicate))
    } yield vcell

  private def computerInput(b: Board, players: List[Player], emptyCells: List[Cell]): F[Cell] =
    (for {
      bm <- Brain.getNextMove(b, players).pure
      cell <- bm.fold(randomComputerInput(emptyCells))(_.pure)
    } yield cell).flatTap(_ => console.println("Computer turn: Press ENTER to continue")) <* console.readLine

  private def randomComputerInput(emptyCells: List[Cell]): F[Cell] =
    for {
      r <- Random.scalaUtilRandom[F]
      b <- r.nextIntBounded(emptyCells.size)
    } yield emptyCells(b)

  private def gameResult(status: Status): F[String] = status match {
    case Status.Draw => "Game ended in draw, better luck next time!!".pure
    case Status.Completed(Player(_, true)) => "Computer won, you lost".pure
    case _ => "Yay!! You won!!".pure
  }




