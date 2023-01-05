package com.satish.app
import cats.Monad
import cats.effect.Sync
import cats.effect.std.Random as CatsRandom
import cats.effect.{IO, IOApp}
import com.satish.app.domain.{Board, Cell, Game, Piece, Player, Status}
import com.satish.app.services.Brain
import com.satish.app.effects.{Console, Random}
import cats.syntax.all.*

object TicTacToe extends IOApp.Simple:

  def run: IO[Unit] =
    IO.println("Hello World!")
    val console = Console.make[IO]
    val randEffect: IO[CatsRandom[IO]] = CatsRandom.scalaUtilRandom[IO]
    for{
      r <- randEffect
      cliTicTocToe = new TicTacToeCli[IO](console, Random.make[IO](r))
      game <- cliTicTocToe.runGameToCompletion
      _ <- cliTicTocToe.printGameStatus(game)
    } yield ()

end TicTacToe

class TicTacToeCli[F[_]: Sync](console: Console[F], random: Random[F]):

  def runGameToCompletion: F[Game] =
    for {
      _ <- console.printLine("game setup start..")
      game <- gameSetup
      _ <- console.printLine("Setup done -> starting game..")
      game <- gameLoop(game)
    } yield game

  def printGameStatus(game: Game): F[Unit] =
    for {
      _ <- console.printLine("Final Board...")
      _ <- console.printLine(game.board.prettyPrint)
      mess <- gameResult(game.status)
      _ <- console.printLine(mess)
    } yield ()

  private def gameSetup: F[Game] = for {
    piece <- getUserPiecePreference
    human = Player(piece, false)
    computer = if piece == Piece.X then Player(Piece.O, true) else Player(Piece.X, true)
    _ <- console.printLine("toss start..")
    tossWinner <- toss(human, computer)
    _ <- console.printLine("toss done..")
  } yield Game(Board.empty, tossWinner, (computer, human))

  private def getUserPiecePreference: F[Piece] =
    for {
      _ <- console.printLine("Enter X or O to choose your piece")
      p <- console.readLine.map(Piece(_))
      piece <- p.fold(getUserPiecePreference)(_.pure)
    } yield piece

  private def toss(p1: Player, p2: Player): F[Player] =
    for {
      p <- random.nextBoolean.map(if _ then p1 else p2)
      _ <- console.printLine(s"Toss won by ${p}, press ENTER to continue")
    } yield p

  private def gameLoop(game: Game): F[Game] =
    game.status match {
      case Status.Ongoing => for {
        _ <- console.printLine(s"game status ${game.status}")
        _ <- console.printLine(game.board.prettyPrint)
        c <- getPlayerInput(game.board, game.allPlayers, game.current, game.board.emptyCells)
        newGame <- game.move(c).pure
        g <- gameLoop(newGame)
      } yield g
      case _ => console.printLine(s"game status ${game.status}") *> game.pure
    }

  private def getPlayerInput(b: Board, players: List[Player], current: Player, emptyCells: List[Cell]): F[Cell] =
    current match {
      case Player(_, true) => computerInput(b, players, emptyCells)
      case _ => userInput(current, c => emptyCells.contains(c))
    }


  private def userInput(p: Player, predicate: Cell => Boolean): F[Cell] =
    for {
      raw <- console.printLine("Enter the cell number") *> console.readLine.map(Cell.fromString)
      cell <- raw.fold(console.printLine("wrong format") *> userInput(p, predicate))(_.pure)
      vcell <- predicate(cell).pure.flatMap(if _ then cell.pure else console.printLine("cell occupied") *> userInput(p, predicate))
    } yield vcell

  private def computerInput(b: Board, players: List[Player], emptyCells: List[Cell]): F[Cell] =
    (for {
      bm <- Brain.getNextMove(b, players).pure
      cell <- bm.fold(randomComputerInput(emptyCells))(_.pure)
    } yield cell).flatTap(_ => console.printLine("Computer turn: Press ENTER to continue")) <* console.readLine

  private def randomComputerInput(emptyCells: List[Cell]): F[Cell] =
    random.nextInt(emptyCells.size).map(emptyCells(_))

  private def gameResult(status: Status): F[String] = status match {
    case Status.Ongoing => "Game is still ongoing".pure
    case Status.Draw => "Game ended in draw, better luck next time!!".pure
    case Status.Completed(Player(_, true)) => "Computer won, you lost".pure
    case _ => "Yay!! You won!!".pure
  }




