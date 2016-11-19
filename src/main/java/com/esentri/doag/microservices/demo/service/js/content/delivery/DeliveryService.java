package com.esentri.doag.microservices.demo.service.js.content.delivery;

import com.hazelcast.core.HazelcastInstance;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class DeliveryService extends AbstractVerticle {
  
  private static final String HAZELCAST_INSTANCE_NAME = "doag-verx-demo-cluster";
  
  private static final String BRIDGE_PATH = "/eventbus/*";
  
  private static final String WEB_ROOT = "src/main/resources/webroot/app";
  
  private static final int PORT = 8080;
  
  private final HazelcastUtil hazelcastUtil = new HazelcastUtil();
  
  private Logger logger;
  
  private Vertx vertx;
  
  public void start(Future<Void> completionFuture) {
    logger = LoggerFactory.getLogger(DeliveryService.class);
    
    Future<HazelcastInstance> hazelcastInstanceFuture = Future.future();
    hazelcastInstanceFuture.setHandler(ref -> clusterCreatedHandler(ref, completionFuture));
    hazelcastUtil.createHazelcastCluster(hazelcastInstanceFuture);
  }
  
  private void initializeDeliveryService(Future<Void> completionFuture) {
  
    logger.debug("initializing server");
    Router router = Router.router(this.vertx);
    BridgeOptions bridgeOptions = new BridgeOptions().addInboundPermitted(new PermittedOptions())
            .addOutboundPermitted(new PermittedOptions());
    SockJSHandler ebHandler = SockJSHandler.create(this.vertx).bridge(bridgeOptions);
    router.route(BRIDGE_PATH).handler(ebHandler);
   
    router.route().handler(StaticHandler.create().setWebRoot(WEB_ROOT).setCachingEnabled(true));
  
  
    HttpServer httpServer = this.vertx.createHttpServer();
    httpServer.requestHandler(router::accept).listen(PORT);
  
    logger.info("HTTP Port: " + httpServer.actualPort());
    logger.info("Server is running...");
    completionFuture.complete();
  }
  
  private void clusterCreatedHandler (AsyncResult<HazelcastInstance> hazelcastInstanceAsyncResult, Future<Void>
          completionFuture) {
    
    if (!hazelcastInstanceAsyncResult.succeeded()) {
      throw new IllegalStateException("Error while creating hazelcast cluster");
    }
    
    ClusterManager clusterManager = new HazelcastClusterManager(hazelcastInstanceAsyncResult.result());
    
    VertxOptions vertxOptions = new VertxOptions();
    vertxOptions.setClusterManager(clusterManager);
    Vertx.clusteredVertx(vertxOptions, res -> {
      if (res.succeeded()) {
        this.vertx = res.result();
        initializeDeliveryService(completionFuture);
      } else {
        completionFuture.fail("Failed: " + res.cause());
      }
    });
  }
  
  
}
