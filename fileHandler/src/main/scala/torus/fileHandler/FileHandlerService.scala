package torus.fileHandler

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.File

import Utils.{FileInfo, Files}

import scala.concurrent.{ExecutionContextExecutor, Future}
import spray.json.DefaultJsonProtocol
import torus.common.CommonConfig

trait Protocols extends DefaultJsonProtocol {
  implicit val fileInfoFormat = jsonFormat2(FileInfo.apply)
}

trait Service extends Protocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter


  def fetchFiles(user: String): Future[Array[FileInfo]] = {
    Future {
      Files
        .listFiles(new File(s"${CommonConfig.torus.getString("data.path")}/$user"))
        .map(a => FileInfo(a.getName, a.getAbsolutePath))
    }
  }

  def createFolder(user: String): Future[Either[String, String]] = {
      new File(s"${CommonConfig.torus.getString("data.path")}/$user").mkdirs() match {
        case true => Future.successful(Right("Folder created"))
        case _ => Future.successful(Left("Folder already exist"))
      }
  }


  val routes = {
    logRequestResult("file-handler-http") {
      pathPrefix("list") {
        (get & path(Segment)) { user =>
          complete {
            fetchFiles(user).map[ToResponseMarshallable] {
              case fileInfo => fileInfo
            }
          }
        }
      } ~
      pathPrefix("createFolder") {
        (get & path(Segment)) { user =>
          complete {
            createFolder(user).map[ToResponseMarshallable] {
              case Right(success) => success
              case Left(error) => error
            }
          }
        }
      }
    }
  }

}

object FileHandlerService extends App with Service {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)


  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))

}
