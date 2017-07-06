package com.xebia.webshop.order.api

import com.xebia.webshop.order.api.Order.{OrderLine, ShippingInfo}
import play.api.libs.json.{Format, Json}

case class Order(id: String, orderLines: Seq[OrderLine], shippingInformation: ShippingInfo)

object Order {

  case class ShippingInfo(address: String, zipcode: String, city: String, country: String)

  case class SKU(id: String)

  case class OrderLine(sku: SKU, amount: Int)

  implicit val skuFormat: Format[SKU] = Json.format
  implicit val orderLineFormat: Format[OrderLine] = Json.format
  implicit val shippingInfoFormat: Format[ShippingInfo] = Json.format
  implicit val format: Format[Order] = Json.format
}

