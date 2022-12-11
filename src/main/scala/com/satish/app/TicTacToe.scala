package com.satish.app
import cats.effect.{IO, IOApp}

object TicTacToe extends IOApp.Simple:
  def run: IO[Unit] =
    IO.println("Hello World!")


