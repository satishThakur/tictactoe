package com.satish.app
import cats.Monad
import cats.effect.Sync
import cats.effect.{IO, IOApp}
import com.satish.app.domain.{Board, Cell, Game, Piece, Player, Status}
import com.satish.app.services.Brain
import com.satish.app.effects.{Console, Random}
import cats.syntax.all.*

object TicTacToe extends IOApp.Simple:

  def run: IO[Unit] =
    val cliTicTocToe = new TicTacToeCli[IO]
    for{
      game <- cliTicTocToe.runGameToCompletion
      _ <- cliTicTocToe.printGameStatus(game)
    } yield ()

end TicTacToe

class TicTacToeCli[F[_]: Sync : Console : Random]:

  def runGameToCompletion: F[Game] =
    for {
      _ <- Console[F].printLine("game setup start..")
      game <- gameSetup
      _ <- Console[F].printLine("Setup done -> starting game..")
      game <- gameLoop(game)
    } yield game

  def printGameStatus(game: Game): F[Unit] =
    for {
      _ <- Console[F].printLine("Final Board...")
      _ <- Console[F].printLine(game.board.prettyPrint)
      mess <- gameResult(game.status)
      _ <- Console[F].printLine(mess)
    } yield ()

  private def gameSetup: F[Game] = for {
    piece <- getUserPiecePreference
    human = Player(piece, false)
    computer = if piece == Piece.X then Player(Piece.O, true) else Player(Piece.X, true)
    _ <- Console[F].printLine("toss start..")
    tossWinner <- toss(human, computer)
    _ <- Console[F].printLine("toss done..")
  } yield Game(Board.empty, tossWinner, (computer, human))

  private def getUserPiecePreference: F[Piece] =
    for {
      _ <- Console[F].printLine("Enter X or O to choose your piece")
      p <- Console[F].readLine.map(Piece(_))
      piece <- p.fold(getUserPiecePreference)(_.pure)
    } yield piece

  private def toss(p1: Player, p2: Player): F[Player] =
    for {
      p <- Random[F].nextBoolean.map(if _ then p1 else p2)
      _ <- Console[F].printLine(s"Toss won by $p, press ENTER to continue")
    } yield p

  private def gameLoop(game: Game): F[Game] =
    game.status match {
      case Status.Ongoing => for {
        _ <- Console[F].printLine(s"game status ${game.status}")
        _ <- Console[F].printLine(game.board.prettyPrint)
        c <- getPlayerInput(game.board, game.allPlayers, game.current, game.board.emptyCells)
        newGame <- game.move(c).pure
        g <- gameLoop(newGame)
      } yield g
      case _ => Console[F].printLine(s"game status ${game.status}") *> game.pure
    }

  private def getPlayerInput(b: Board, players: List[Player], current: Player, emptyCells: List[Cell]): F[Cell] =
    current match {
      case Player(_, true) => computerInput(b, players, emptyCells)
      case _ => userInput(current, c => emptyCells.contains(c))
    }


  private def userInput(p: Player, predicate: Cell => Boolean): F[Cell] =
    for {
      raw <- Console[F].printLine("Enter the cell number") *> Console[F].readLine.map(Cell.fromString)
      cell <- raw.fold(Console[F].printLine("wrong format") *> userInput(p, predicate))(_.pure)
      vcell <- predicate(cell).pure.flatMap(if _ then cell.pure else Console[F].printLine("cell occupied") *> userInput(p, predicate))
    } yield vcell

  private def computerInput(b: Board, players: List[Player], emptyCells: List[Cell]): F[Cell] =
    (for {
      bm <- Brain.getNextMove(b, players).pure
      cell <- bm.fold(randomComputerInput(emptyCells))(_.pure)
    } yield cell).flatTap(_ => Console[F].printLine("Computer turn: Press ENTER to continue")) <* Console[F].readLine

  private def randomComputerInput(emptyCells: List[Cell]): F[Cell] =
    Random[F].nextInt(emptyCells.size).map(emptyCells(_))

  private def gameResult(status: Status): F[String] = status match {
    case Status.Ongoing => "Game is still ongoing".pure
    case Status.Draw => "Game ended in draw, better luck next time!!".pure
    case Status.Completed(Player(_, true)) => "Computer won, you lost".pure
    case _ => "Yay!! You won!!".pure
  }




