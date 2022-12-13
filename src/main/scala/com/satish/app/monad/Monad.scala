package com.satish.app.monad

trait Monad[F[_]]:

  def unit[A](a: A): F[A]

  extension[A](fa: F[A])
    def map[B](f : A => B): F[B]

    def flatMap[B](f : A => F[B]): F[B]

    def map2[B,C](fb : F[B])(f: (A,B) => C) : F[C]



object Monad{
  //TODO - create state Monad here!!
}
