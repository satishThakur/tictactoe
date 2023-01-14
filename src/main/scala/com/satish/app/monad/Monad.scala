package com.satish.app.monad
import com.satish.app.state.State
trait Functor[F[_]]:
  extension [A](fa: F[A]) def map[B](f: A => B): F[B]

trait Monad[F[_]] extends Functor[F]:

  def unit[A](a: A): F[A]

  extension [A](fa: F[A])
    def map[B](f: A => B): F[B] = flatMap(a => unit(f(a)))

    def flatMap[B](f: A => F[B]): F[B]

    def map2[B, C](fb: F[B])(f: (A, B) => C): F[C] =
      flatMap(a => fb.map(b => f(a, b)))

object Monad {
  given stateMonad[S]: Monad[State[S, _]] with
    def unit[A](a: A): State[S, A]                                                  = State.unit(a)
    extension [A](fa: State[S, A]) def flatMap[B](f: A => State[S, B]): State[S, B] = State.flatMap(fa)(f)
}
