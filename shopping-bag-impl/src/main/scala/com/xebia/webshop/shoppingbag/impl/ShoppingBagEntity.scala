package com.xebia.webshop.shoppingbag.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import com.xebia.webshop.shoppingbag.impl.PShoppingBagCommand.{AddItem, GetBasket, RemoveItem}
import com.xebia.webshop.shoppingbag.impl.PShoppingBagEvent.{ItemAdded, ItemRemoved}
import play.api.libs.json.{Format, Json}

object ShoppingBagEntityFormats {
  implicit val pShoppingBagFormat: Format[PShoppingBag] = Json.format

  implicit val addItemFormat: Format[AddItem] = Json.format
  implicit val removeItemFormat: Format[RemoveItem] = Json.format
  implicit val getBasketFormat: Format[GetBasket.type] = JsonSerializer.emptySingletonFormat(GetBasket)

  implicit val itemAdded: Format[ItemAdded] = Json.format
  implicit val itemRemoved: Format[ItemRemoved] = Json.format
}

case class PShoppingBag(items: Map[String, Int])

final class ShoppingBagEntity extends PersistentEntity {
  override type Command = PShoppingBagCommand
  override type Event = PShoppingBagEvent
  override type State = PShoppingBag

  override def initialState = PShoppingBag(Map.empty)

  override def behavior: Behavior = {
    Actions().onCommand[PShoppingBagCommand.AddItem, Done] {
      case (PShoppingBagCommand.AddItem(sku, amount), ctx, state) => {
        ctx.thenPersist(PShoppingBagEvent.ItemAdded(sku, amount))(_ => ctx.reply(Done))
      }
    }.onCommand[PShoppingBagCommand.RemoveItem, Done] {
      case x => ???
    }.onReadOnlyCommand[PShoppingBagCommand.GetBasket.type, PShoppingBag] {
      case (PShoppingBagCommand.GetBasket, ctx, state) => ctx.reply(state)
    }.onEvent {
      case (ItemAdded(sku, amount), state) => ShoppingBagEntity.addItemToShoppingBag(state, sku, amount)
      case (ItemRemoved(sku, amount), state) => ???
    }
  }
}

object ShoppingBagEntity {
  def addItemToShoppingBag(shoppingBag: PShoppingBag, sku: String, amount: Int): PShoppingBag =
    shoppingBag.copy(items = shoppingBag.items + (sku -> (shoppingBag.items.getOrElse(sku, 0) + amount)))
}
