package utils

sealed class Either<T, U>  {

    class Left<T> (val value: T) : Either<T, Nothing>()
    class Right<U> (val value: U) : Either<Nothing, U>()

}