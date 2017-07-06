package com.xebia.webshop.shoppingbag.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.xebia.webshop.shoppingbag.api.{AddItemToShoppingBagRequest, ShoppingBag, ShoppingBagService}
import com.xebia.webshop.shoppingbag.impl.PShoppingBagCommand.{AddItem, GetBasket}

import scala.concurrent.ExecutionContext

class ShoppingBagServiceImpl(persistentEntities: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends ShoppingBagService {
  override def retrieveShoppingBag(basketId: String): ServiceCall[NotUsed, ShoppingBag] = ServiceCall { req =>
    persistentEntities.refFor[ShoppingBagEntity](basketId).ask(GetBasket).map(x => ShoppingBag(x.items))
  }

  override def addItemToBag(basketId: String): ServiceCall[AddItemToShoppingBagRequest, ShoppingBag] = ServiceCall { addItem =>
    persistentEntities.refFor[ShoppingBagEntity](basketId).ask(AddItem(addItem.sku, addItem.amount))
      .flatMap(_ => persistentEntities.refFor[ShoppingBagEntity](basketId).ask(GetBasket).map(x => ShoppingBag(x.items)))
  }

  override def removeItemFromBag(basketId: String): ServiceCall[NotUsed, NotUsed] = ???
}