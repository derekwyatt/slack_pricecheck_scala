import dispatch._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

object ITAD {
  def apply(token: String): ITAD = {
    new ITAD(token)
  }
}

class ITAD(token: String) {

  def prices(gamePlain: String): Option[JsValue] = {
    val svc = url(s"https://api.isthereanydeal.com/v01/game/prices?key=$token&plains=$gamePlain&country=CA")
    val pricesHtml = Http(svc OK as.String)
    val pricesJson = Json.parse(pricesHtml())
    val pricesList = (pricesJson \ "data" \ gamePlain \ "list" ).asOpt[JsValue]
    return pricesList
  }

  def getPlain(gameTitle: String): Option[String] = {
    val svc = url(s"https://api.isthereanydeal.com/v02/game/plain/?key=$token&title=$gameTitle")
    val gamePlain = Http(svc OK as.String)
    val plain = Json.parse(gamePlain())
    (plain \ "data" \ "plain").asOpt[String]
  }
}
