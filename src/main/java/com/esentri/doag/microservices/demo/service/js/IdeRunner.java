package com.esentri.doag.microservices.demo.service.js;


import com.esentri.doag.microservices.demo.service.js.content.delivery.DeliveryService;
import io.vertx.core.Vertx;

public class IdeRunner {
  
  public static void main(String[] args) {
  
    Vertx.vertx().deployVerticle(DeliveryService.class.getName(), onComletion -> {
      if(!onComletion.succeeded()) {
        throw new RuntimeException("Error while deploying DeliveryService");
      }
      System.out.println("DeliveryService deployed!");
    });
  }
}
