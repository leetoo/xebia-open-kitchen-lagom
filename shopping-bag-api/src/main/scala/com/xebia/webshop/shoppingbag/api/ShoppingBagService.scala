package com.xebia.webshop.shoppingbag.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport._
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

case class ShoppingBag(items: Map[String, Int])

object ShoppingBag {
  implicit val format: Format[ShoppingBag] = Json.format
}

trait ShoppingBagService extends Service {

  import Service._

  def addItemToBag(basketId: String): ServiceCall[AddItemToShoppingBagRequest, ShoppingBag]

  def removeItemFromBag(basketId: String): ServiceCall[NotUsed, NotUsed]

  def retrieveShoppingBag(basketId: String): ServiceCall[NotUsed, ShoppingBag]

  final override def descriptor = {
    named("shopping-bag").withCalls(
      restCall(Method.POST, "/api/shoppingBag/:basketId/items", addItemToBag _),
      restCall(Method.GET, "/api/shoppingBag/:basketId", retrieveShoppingBag _)
    )
  }
}