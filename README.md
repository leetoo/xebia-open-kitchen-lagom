https://www.youtube.com/watch?v=f1YSSaI_J-c&t=29s

# Step 1

The **Shopping Bag Entity** service is currently missing functionality to remove items from an existing _shopping bag_, 
add this functionality to the Shopping Bag Entity and ensure the tests are working correctly 
(what happens when a shopping bag doesn't contain the actual SKU which is removed or lowered?)

# Step 2

The **Order Entity** contains all functionality to handle incoming commands. The service implementation is, however, 
currently missing the code necessary to have an implementation of the interface it exposes. Add the required functionality
and accompanied mappings towards the service implementation and adjust the tests accordingly. 

In the current implementation, the creation and confirmation of orders will push domain events towards a topic with the 
`OrderEvent` type. Implement these test to check if this behaviour is correctly working.

# Step 3
Add an additional, separate, Scala application which runs separate from your Microservice architecture 
(you can start the services by using the `sbt runAll` command.) which uses the Service clients (like defined in the tests) 
  to test the services behaviour in harmony. 
 
