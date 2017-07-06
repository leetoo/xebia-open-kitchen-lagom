package com.xebia.webshop.shoppingbag.impl

sealed trait PShoppingBagEvent

object PShoppingBagEvent {

  case class ItemAdded(sku: String, amount: Int) extends PShoppingBagEvent

  case class ItemRemoved(sku: String, amount: Int) extends PShoppingBagEvent

}