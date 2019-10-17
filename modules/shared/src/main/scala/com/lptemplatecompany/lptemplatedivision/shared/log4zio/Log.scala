package com.lptemplatecompany.lptemplatedivision.shared.log4zio

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import zio.{ App, Task, ZIO }

trait Log {
  def log: Log.Service
}

object Log {
  def log: ZIO[Log, Nothing, Log.Service] =
    ZIO.access[Log](_.log)

  /**
   * This implementation assumes that the user doesn't want to experience logging failures. Logging is
   * most important under failure conditions, so it is best to log via a fallback mechanism rather than
   * fail altogether. Hence error type `Nothing`. It is the responsibility of `Service` implementations
   * to implement fallback behaviour.
   */
  trait Service {
    def log: LogStep => ZIO[Any, Nothing, Unit]

    //// shortcuts

    def error(message: => String): ZIO[Any, Nothing, Unit] =
      log(Log.error(message))

    def warn(message: => String): ZIO[Any, Nothing, Unit] =
      log(Log.warn(message))

    def info(message: => String): ZIO[Any, Nothing, Unit] =
      log(Log.info(message))

    def debug(message: => String): ZIO[Any, Nothing, Unit] =
      log(Log.debug(message))
  }

  def console: ZIO[Any, Nothing, Log] =
    ZIO.effectTotal {
      new Log {
        override def log: Service =
          new Service {
            val zioConsole = zio.console.Console.Live.console
            val timestampFormat: DateTimeFormatter =
              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            override def log: LogStep => ZIO[Any, Nothing, Unit] =
              l =>
                zioConsole.putStrLn(
                  "%s %-5s - %s"
                    .format(timestampFormat.format(LocalDateTime.now), l.level, l.message())
                )
          }
      }
    }

  def slf4j: ZIO[Any, Nothing, Log] =
    ZIO.effect {
      org.slf4j.LoggerFactory.getLogger(getClass)
    }.map {
      slfLogger =>
        new Log {
          override def log: Service =
            new Service {
              override def log: LogStep => ZIO[Any, Nothing, Unit] =
                entry => {
                  val result: Task[Unit] =
                    entry match {
                      case Error(message) =>
                        ZIO.effect(slfLogger.error(message()))
                      case Warn(message) =>
                        ZIO.effect(slfLogger.warn(message()))
                      case Info(message) =>
                        ZIO.effect(slfLogger.info(message()))
                      case Debug(message) =>
                        ZIO.effect(slfLogger.debug(message()))
                    }

                  result.catchAll(_ => console.flatMap(_.log.log(entry))) // fallback on failure
                }
            }
        }
    }.catchAll(_ => console) // fallback on failure

  def silent: ZIO[Any, Nothing, Log] =
    ZIO.effectTotal {
      new Log {
        override def log: Service =
          new Service {
            override def log: LogStep => ZIO[Any, Nothing, Unit] =
              _ => ZIO.unit
          }
      }
    }

  sealed trait LogStep {
    def message: () => String
    val level: String
  }

  final case class Error(message: () => String) extends LogStep {
    override val level: String = "ERROR"
  }
  final case class Warn(message: () => String) extends LogStep {
    override val level: String = "WARN"
  }
  final case class Info(message: () => String) extends LogStep {
    override val level: String = "INFO"
  }
  final case class Debug(message: () => String) extends LogStep {
    override val level: String = "DEBUG"
  }

  def error(message: => String): LogStep =
    Error(() => message)
  def warn(message: => String): LogStep =
    Warn(() => message)
  def info(message: => String): LogStep =
    Info(() => message)
  def debug(message: => String): LogStep =
    Debug(() => message)

}

object FreeV extends App {

  def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {

    val program: ZIO[Log, Throwable, Unit] =
      ZIO.accessM[Log] {
        env =>
          for {
            _ <- env.log.error("Test string ... error")
            _ <- env.log.warn("Test string ... warn")
            _ <- env.log.info("Test string ... info")
            _ <- env.log.debug("Test string ... debug")
          } yield ()
      }

    for {
      logEnv <- Log.slf4j
      exitCode <- program
        .provide(logEnv)
        .foldM(failure = _ => ZIO.succeed(1), success = _ => ZIO.succeed(0))
    } yield exitCode
  }
}
