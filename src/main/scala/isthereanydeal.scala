import dispatch._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

object ITAD {
  def apply(token: String): ITAD = {
    new ITAD(token)
  }
}

class ITAD(token: String) {

  implicit val shopReader = new Reads[Shop] {
    def reads(js: JsValue): JsResult[Shop] = {
      JsSuccess(Shop(
        (js \ "id").as[String],
        (js \ "name").as[String]
      ))
    }

  }

  implicit val priceReader = new Reads[Price] {
    def reads(js: JsValue): JsResult[Price] = {
      JsSuccess(Price(
        (js \ "price_new").as[Double],
        (js \ "price_old").as[Double],
        (js \ "price_cut").as[Double],
        (js \ "url").as[String],
        (js \ "shop").as[Shop]
      ))
    }
  }

  def prices(gamePlain: String): Option[List[Price]] = {
    val svc = url(s"https://api.isthereanydeal.com/v01/game/prices?key=$token&plains=$gamePlain&country=CA")
    val pricesHtml = Http(svc OK as.String)
    val pricesJson = Json.parse(pricesHtml())
    val pricesList = (pricesJson \ "data" \ gamePlain \ "list" ).asOpt[List[Price]]
    return pricesList
  }

  def getPlain(gameTitle: String): Option[String] = {
    val svc = url(s"https://api.isthereanydeal.com/v02/game/plain/?key=$token&title=$gameTitle")
    val gamePlain = Http(svc OK as.String)
    val plain = Json.parse(gamePlain())
    (plain \ "data" \ "plain").asOpt[String]
  }
  
}


case class Shop(id: String, name: String)
case class Price(
  price_new: Double, 
  price_old: Double, 
  price_cut: Double, 
  url: String,
  shop: Shop
)
