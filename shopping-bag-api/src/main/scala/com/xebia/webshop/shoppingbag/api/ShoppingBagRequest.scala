package com.xebia.webshop.shoppingbag.api

import play.api.libs.json.{Format, Json}

case class AddItemToShoppingBagRequest(sku: String, amount: Int)

object AddItemToShoppingBagRequest {
  implicit val format: Format[AddItemToShoppingBagRequest] = Json.format
}