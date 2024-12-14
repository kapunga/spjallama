package spjallama.core.character

import cats.MonadThrow
import cats.syntax.all.given
import java.io.File
import org.ekrich.config.{Config, ConfigFactory}
import scala.util.Try
import spjallama.core.config.{*, given}

case class CharacterRegistry(characters: Map[String, Character])

object CharacterRegistry:
  def apply[F[_]](using monad: MonadThrow[F]): F[CharacterRegistry] =
    monad.fromTry(Try(ConfigFactory.load())).flatMap(fromConfig)
    
  def fromFile[F[_]](configFile: String)(using monad: MonadThrow[F]): F[CharacterRegistry] =
    for {
      config <- monad.fromTry(Try(ConfigFactory.parseFile(new File(configFile))))
      registry <- fromConfig(config)
    } yield registry
    
  def fromConfig[F[_]](config: Config)(using monad: MonadThrow[F]): F[CharacterRegistry] =
    monad.fromEither(config.get[List[Character]]("characters")).map(fromList)
    
  def fromList(characters: List[Character]): CharacterRegistry =
    CharacterRegistry(characters.map(c => c.name -> c).toMap)
