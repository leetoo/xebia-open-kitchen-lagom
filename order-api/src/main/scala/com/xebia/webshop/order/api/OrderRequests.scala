package com.xebia.webshop.order.api

import play.api.libs.json.{Format, Json}

case class CreateOrderRequest(items: Map[String, Int])

object CreateOrderRequest {
  implicit val format: Format[CreateOrderRequest] = Json.format
}

case class ConfirmOrderRequest(paymentReference: String)

object ConfirmOrderRequest {
  implicit val format: Format[ConfirmOrderRequest] = Json.format
}
