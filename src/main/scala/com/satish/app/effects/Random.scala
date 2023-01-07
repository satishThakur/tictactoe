package com.satish.app.effects
import cats.effect.std.Random as CatsRandom
import cats.effect.IO
import cats.effect.kernel.Sync
import cats.syntax.all.*

trait Random[F[_]]:
  def nextInt(upper: Int): F[Int]
  def nextBoolean: F[Boolean]


object Random:

  def apply[F[_]](using Random[F]): Random[F] = summon[Random[F]]

  given r[F[_]: Sync]: Random[F] with
    def nextInt(upper: Int): F[Int] = CatsRandom.scalaUtilRandom[F].flatMap(_.nextIntBounded(upper))
    def nextBoolean: F[Boolean] = CatsRandom.scalaUtilRandom[F].flatMap(_.nextBoolean)



  def make[F[_]](r : CatsRandom[F]): Random[F] = new Random[F]:
    def nextInt(upper: Int): F[Int] = r.nextIntBounded(upper)
    def nextBoolean: F[Boolean] = r.nextBoolean

  def makeTest[F[_]](nf: Int => F[Int], bf: F[Boolean]): Random[F] = new Random[F]:
    def nextInt(upper: Int): F[Int] = nf(upper)
    def nextBoolean: F[Boolean] = bf
