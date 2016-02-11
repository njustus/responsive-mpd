import scala.annotation.tailrec
import scala.concurrent.Future

import java.net.{ ConnectException, NoRouteToHostException }

import org.bff.javampd.exception.{ MPDConnectionException, MPDException, MPDPlayerException, MPDTimeoutException }

import javax.inject.{ Inject, Provider }
import play.api.{ Configuration, Environment, OptionalSourceMapper, UsefulException }
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{ RequestHeader, Result }
import play.api.mvc.Results.InternalServerError
import play.api.routing.Router

class ErrorHandler @Inject() (
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router]
  ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  private val modeKey = "application.mode"
  private lazy val possibleModes = Some(Set("prod", "dev", "PROD", "DEV"))

  def isDevelopingMode:Boolean = {
    (for {
      str <- config.getString(modeKey, possibleModes)
      if str == "dev" || str == "DEV"
    } yield true).getOrElse(false)
  }

  def isProductionMode:Boolean = !isDevelopingMode

  override def onDevServerError(request: RequestHeader, exception: UsefulException) =
    if(isProductionMode) onProdServerError(request, exception)
    else super.onDevServerError(request, exception)

  override def onProdServerError(request: RequestHeader, exception: UsefulException) =
    Future {
      exceptionHandler(exception.cause)
  }

  private def exceptionHandler: PartialFunction[Throwable, Result] =
      mpdException.orElse(otherExceptions).andThen { msg => InternalServerError(views.html.error(msg)) }

  @tailrec
  private def getInnerstException(t:Throwable): Throwable = {
    if(t.getCause != null) getInnerstException(t.getCause)
    else t
  }

  private def mpdException: PartialFunction[Throwable, String] =
    getInnerstException(_) match {
      case exc:ConnectException => "Can't establish connection to the server:\n" + exc.getMessage
      case exc:NoRouteToHostException => "Can't connect to the server:\n" + exc.getMessage
      case exc:MPDPlayerException =>
        "Player exception: " + exc.getMessage
      case exc:MPDTimeoutException => "Timeout exc"
      case exc:MPDConnectionException => "An error occured while connecting to the mpd server:\n " + exc.getMessage
      case exc:MPDException => "An unknown error from mpd occured:\n" + exc.getMessage
    }

  private def otherExceptions: PartialFunction[Throwable, String] = {
    case null => "An unknown exception occured!"
    case exc:Throwable => "System totally corrupted!\nPlease consult the developers and report this message:\n" + exc.toString()
  }
}
