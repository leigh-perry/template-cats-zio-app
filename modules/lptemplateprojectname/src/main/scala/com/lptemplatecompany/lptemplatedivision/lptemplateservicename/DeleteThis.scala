package xxxxx

import scalaz.zio.console.Console
import scalaz.zio._

sealed trait AppError
case object NoValue extends AppError

trait Logger {
  val logger: Logger.Service
}
object Logger {
  trait Service {
    def info(line: String): UIO[Unit]
  }
  trait Live extends Logger {
    val logger: Service = new Service { // 敢えて SAM 無視
      def info(line: String): UIO[Unit] = UIO.effectTotal(println(line))
    }
  }
  def info(line: String): ZIO[Logger, Nothing, Unit] =
    ZIO.accessM(_.logger info s"INFO: $line")
}

trait KVStore {
  val kvStore: KVStore.Service
}
object KVStore {
  trait Service {
    def valueOf(key: String): IO[AppError, String]
  }
  trait Live extends KVStore {
    private val dummy: Map[String, String] = Map("42" -> "Foo")
    val kvStore: Service = new Service { // 敢えて SAM 無視
      def valueOf(key: String): IO[AppError, String] =
        IO.fromEither(dummy.get(key).toRight[AppError](NoValue))
    }
  }
  def valueOf(key: String): ZIO[KVStore, AppError, String] =
    ZIO.accessM(_.kvStore valueOf key)
}

import scalaz.zio.console._
import KVStore._
import Logger._

trait Env extends Console with Logger with KVStore
object Env extends Console.Live with Logger.Live with KVStore.Live

object Main extends App {
  type AppType = Console with Logger with KVStore

  val program: ZIO[AppType, AppError, Unit] = for {
    key <- getStrLn.orDie
    value <- valueOf(key)
    _ <- info(s"$key -> $value")
  } yield ()

  def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    program.provide(Env).fold(
      e => {
        println(e); -1
      },
      _ => 0
    )
}

object MainSpec {
  object testEnv extends Env {
    val console: Console.Service[Any] = ???
    val logger: Logger.Service = ???
    val kvStore: KVStore.Service = ???
  }
  // Main.program.provide(testEnv) をテストするコードいろいろ
}

