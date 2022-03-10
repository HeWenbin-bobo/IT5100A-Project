package or

/**
  * Represents a right-biased, fail-fast disjoint union. Instances of [[or]] are
  * either instances of [[Right]] or [[Left]]. [[Right]] usually represents
  * the happy path, [[Left]] usually represents the sad path.
  */
sealed trait or[+E, +A] extends Product with Serializable

/**
  * An instance of [[Left]] represents the sad path
  *
  * @param e the sad object
  */
final case class Left[+E](e: E) extends or[E, Nothing]

/**
  * An instance of [[Right]] represents the happy path
  *
  * @param a the happy object
  */
final case class Right[+A](a: A) extends or[Nothing, A]
