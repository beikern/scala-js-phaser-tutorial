package example

import akka.http.scaladsl.server.Directives
import shared.SharedMessages

class WebService() extends Directives {

  val route = {
    pathSingleSlash {
      get {
        complete {
          example.html.index.render(SharedMessages.itWorks)
        }
      }
    } ~
      pathPrefix("assets" / Remaining) { file =>
        getFromResource("public/" + file)
      }
  }
}
