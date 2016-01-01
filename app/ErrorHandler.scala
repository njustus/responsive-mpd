import javax.inject._
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router
import scala.concurrent._
import org.bff.javampd.exception._
import java.net.NoRouteToHostException
import scala.annotation.tailrec
import java.net.ConnectException

class ErrorHandler @Inject() (
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router]
  ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

//  override def onDevServerError(request: RequestHeader, exception: UsefulException) =
//    onProdServerError(request, exception)

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
