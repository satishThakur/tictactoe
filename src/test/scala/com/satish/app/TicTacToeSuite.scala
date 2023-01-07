package com.satish.app

import cats.effect.{IO, Ref}
import com.satish.app.domain.{Piece, Player, Status}
import com.satish.app.effects.{Console, Random}
import weaver.*

object TicTacToeSuite extends SimpleIOSuite{

  test("end to end tictactoe game with computer winning"){

    for {
      reader <- Ref.of[IO, List[String]](List("x","", "5", "", "9", ""))
      writer <- Ref.of[IO, List[String]](List.empty)
      given  Console[IO] = Console.makeTest[IO](reader, writer)
      given  Random[IO] = Random.makeTest[IO](n => IO.pure(0), IO.pure(false))
      board = new TicTacToeCli[IO]
      game <- board.runGameToCompletion
    } yield expect(game.status == Status.Completed(Player(Piece.O, true)))
  }

  test("end to end game with draw"){
    for {
      reader <- Ref.of[IO, List[String]](List("x", "", "5", "", "2", "", "3", "", "9", "", "4", ""))
      writer <- Ref.of[IO, List[String]](List.empty)
      given  Console[IO] = Console.makeTest[IO](reader, writer)
      given  Random[IO] = Random.makeTest[IO](n => IO.pure(0), IO.pure(true))
      board = new TicTacToeCli[IO]
      game <- board.runGameToCompletion
    } yield expect(game.status == Status.Draw)
  }
}

