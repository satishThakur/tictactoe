package com.satish.app.state

/** What State represent is a State transformation function. It takes the current state, produces the value A and
  * trnasition to state to a new state.
  * @tparam S
  *   \- State
  * @tparam A
  *   \- Value
  */
opaque type State[S, A] = S => (A, S)

object State:
  extension [S, A](other: State[S, A])
    /** run finally runs the STF to produce value and modified state. This is mostly done on the edge of the
      * application.
      */
    def run(s: S): (A, S) = other(s)

    /** given a STF and a function which maps A to B - Create new STF which produces B.
      */
    def map[B](f: A => B): State[S, B] =
      s => {
        val (a, ss) = other.run(s)
        (f(a), ss)
      }

    /** given current STF and another STF and a function which maps A,B to C produce a new STF which produces C
      * Logically it runs first STF and then thread the state to second STF and finally applies the function.
      */
    def map2[B, C](sb: State[S, B])(f: (A, B) => C): State[S, C] =
      s => {
        val (a, s1) = other.run(s)
        val (b, s2) = sb.run(s1)
        (f(a, b), s2)
      }

    /** given another STF who depends on value A - produce it. Again state is threaded via both STFs.
      */
    def flatMap[B](sb: A => State[S, B]): State[S, B] =
      s => {
        val (a, s1) = other.run(s)
        sb(a).run(s1)
      }

  end extension

  def unit[S, A](a: A): State[S, A] = s => (a, s)

  def sequence[S, A](ls: List[State[S, A]]): State[S, List[A]] =
    ls.foldRight(unit(Nil: List[A]))((s, acc) => s.map2(acc)(_ :: _))

  def traverse[S, A, B](ls: List[A])(f: A => State[S, B]): State[S, List[B]] =
    ls.foldRight(unit(Nil: List[B]))((a, acc) => f(a).map2(acc)(_ :: _))

  def get[S]: State[S, S] = s => (s, s)

  def set[S](s: S): State[S, Unit] = _ => ((), s)

  def modify[S](f: S => S): State[S, Unit] = for {
    s <- get[S]
    _ <- set[S](f(s))
  } yield ()
