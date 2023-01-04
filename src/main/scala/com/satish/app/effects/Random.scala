package com.satish.app.effects
import cats.effect.std.Random as CatsRandom

trait Random[F[_]]:
  def nextInt(upper: Int): F[Int]
  def nextBoolean: F[Boolean]


object Random:

  def make[F[_]](r : CatsRandom[F]): Random[F] = new Random[F]:
    def nextInt(upper: Int): F[Int] = r.nextIntBounded(upper)
    def nextBoolean: F[Boolean] = r.nextBoolean

  def makeTest[F[_]](nf: Int => F[Int], bf: F[Boolean]): Random[F] = new Random[F]:
    def nextInt(upper: Int): F[Int] = nf(upper)
    def nextBoolean: F[Boolean] = bf