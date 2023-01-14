package com.satish.app.effects
import cats.effect.kernel.{Ref, Sync}
import cats.effect.std.Console as CatsConsole
import cats.syntax.all.*

/** A capability trait to read and write to the console. This is not a typeclass but a capability trait modelled as
  * tagless final.
  * @tparam F
  */
trait Console[F[_]]:
  def printLine(line: String): F[Unit]
  def readLine: F[String]

object Console:

  def apply[F[_]](using Console[F]): Console[F] = summon[Console[F]]

  given ioConsole[F[_]: CatsConsole]: Console[F] with
    def printLine(line: String): F[Unit] = summon[CatsConsole[F]].println(line)
    def readLine: F[String]              = summon[CatsConsole[F]].readLine

  /** Real console which can be used in production.
    * @tparam F
    *   Effect type
    * @return
    *   Console instance
    */
  def make[F[_]: CatsConsole]: Console[F] = new Console[F]:
    def printLine(line: String): F[Unit] = CatsConsole[F].println(line)
    def readLine: F[String]              = CatsConsole[F].readLine

  /** Console which can be used in tests. The console simply reads from a list of strings and writes to a list of
    * strings.
    * @param reader
    *   \- list of strings to read from
    * @param writer
    *   \- list of strings to write to
    * @tparam F
    *   \- Effect type
    * @return
    *   \- Console instance
    */
  def makeTest[F[_]: Sync](reader: Ref[F, List[String]], writer: Ref[F, List[String]]): Console[F] = new Console[F]:
    def printLine(line: String): F[Unit] = Sync[F].delay(println(line)) *> writer.update(line :: _)
    def readLine: F[String]              = reader.modify { case h :: t => (t, h) }
