/*
 * Copyright 2011 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import groovy.util.GroovyTestCase
import org.nodex.java.core.buffer.Buffer
import org.nodex.groovy.core.Nodex
import org.nodex.groovy.core.net.*

class NetTest extends GroovyTestCase {

  void testEcho() {
    def latch = Utils.latch()
    
    // This nonsense is to deal with scope issues
    // I'm not sure how the Ruby test refers to client
    // inside the connect method
    def client
    def closeHandler = {
      client.close()
      latch.countDown()
    }
    
    Nodex.go {
      
      def server = new NetServer().listen(8080) { socket ->
        socket.dataHandler { data ->
          socket.writeBuffer(data)
        }
      }
      
      client = new NetClient().connect(8080, "localhost") { socket ->
      
        def sends = 10
        def size  = 100
        
        def received = Buffer.create(0)
        def sent     = Buffer.create(0)
      
        socket.dataHandler { data -> 
        
          received.appendBuffer(data)
          if (received.length() == sends * size) {
            assertTrue(Utils.buffersEqual(sent, received))
            
            server.close(closeHandler)
          }
        
        }
        
        socket.drainHandler {
        }
        
        socket.endHandler {
        }
        
        (1..sends).each { i ->
          def data = Utils.genBuffer(size)
          sent.appendBuffer(data)
          socket.writeBuffer(data)
        }
      }

    }
    
    assert(latch.await(5.seconds))
  }
  
  void testEchoSSL() {
    def latch = Utils.latch()
    def client
    def closeHandler = {
      client.close()
      latch.countDown()
    }
    
    Nodex.go {
      
      def serverArgs = [
        SSL: true, 
        keyStorePath: "../resources/keystores/server-keystore.jks",
        keyStorePassword: "wibble",
        trustStorePath: "../resources/keystores/server-truststore.jks",
        trustStorePassword: "wibble",
        clientAuthRequired: true
      ]
      
      def server = new NetServer(serverArgs).listen(8080) { socket ->
        socket.dataHandler { data ->
          socket.writeBuffer(data)
        }
      }
      
      def clientArgs = [
        SSL: true,
        keyStorePath: "../resources/keystores/client-keystore.jks",
        keyStorePassword: "wibble",
        trustStorePath: "../resources/keystores/client-truststore.jks",
        trustStorePassword: "wibble",
      ]
      
      client = new NetClient(clientArgs).connect(8080, "localhost") { socket ->
      
        def sends = 10
        def size  = 100
        
        def received = Buffer.create(0)
        def sent     = Buffer.create(0)
      
        socket.dataHandler { data -> 
        
          received.appendBuffer(data)
          if (received.length() == sends * size) {
            assertTrue(Utils.buffersEqual(sent, received))
            
            server.close(closeHandler)
          }
        
        }
        
        socket.drainHandler {
        }
        
        socket.endHandler {
        }
        
        socket.closedHandler {
        }
        
        socket.pause()
        socket.resume()
        socket.writeQueueFull()
        socket.writeQueueMaxSize = 100000
        
        (1..sends).each { i ->
          def data = Utils.genBuffer(size)
          sent.appendBuffer(data)
          socket.writeBuffer(data)
        }
      }
    }
    
    assert(latch.await(5.seconds))
  }

  void testMethods() {
    def latch = Utils.latch()
    
    Nodex.go() {
    
      def server = new NetServer()
      server.SSL = true
      server.keyStorePath = "foo.jks"
      server.keyStorePassword = "blah"
      server.trustStorePath = "foo.jks"
      server.trustStorePassword = "blah"
      server.sendBufferSize = 123123
      server.receiveBufferSize = 123123
      server.TCPKeepAlive = true
      server.reuseAddress = true
      server.soLinger = true
      server.trafficClass = 123
      server.connectHandler { socket -> }
      server.close()

      // Tests that all the methods return 'this' so they can be chained
      assert server instanceof NetServer : "The server should have been a Groovy NetServer to start with"
      server = server.setSSL(true)
        .setKeyStorePath("foo.jks")
        .setKeyStorePassword("blah")
        .setTrustStorePath("foo.jks")
        .setTrustStorePassword("blah")
        .setSendBufferSize(123123)
        .setReceiveBufferSize(123123)
        .setTCPKeepAlive(true)
        .setReuseAddress(true)
        .setSoLinger(true)
        .setTrafficClass(123)
      assert server instanceof NetServer : "The server should have been a Groovy NetServer"

      def client = new NetClient()
      client.SSL = true
      client.keyStorePath = "foo.jks"
      client.keyStorePassword = "blah"
      client.trustStorePath = "foo.jks"
      client.trustStorePassword = "blah"
      client.trustAll = true
      client.sendBufferSize = 123123
      client.receiveBufferSize = 123123
      client.TCPKeepAlive = true
      client.reuseAddress = true
      client.soLinger = true
      client.trafficClass = 123
      client.reconnectAttempts = 5
      client.reconnectInterval = 45
      client.close()
      
      // Tests that all the methods return 'this' so they can be chained
      assert client instanceof NetClient : "The client should have been a Groovy NetClient to start with"
      client = client.setSSL(true)
        .setKeyStorePath("foo.jks")
        .setKeyStorePassword("blah")
        .setTrustStorePath("foo.jks")
        .setTrustStorePassword("blah")
        .setTrustAll(true)
        .setSendBufferSize(123123)
        .setReceiveBufferSize(123123)
        .setTCPKeepAlive(true)
        .setReuseAddress(true)
        .setSoLinger(true)
        .setTrafficClass(123)
        .setReconnectAttempts(5)
        .setReconnectInterval(45)
      assert client instanceof NetClient : "The client should have been a Groovy NetClient"
      
      // Tests the proper delegation to the Java NetClient for getting property values
      assert client.reconnectAttempts == 5 : "ReconnectAttempts should have been 5"
      assert client.reconnectInterval == 45 : "ReconnectInterval should have been 45"
      
      latch.countDown()
    }
    
    assert(latch.await(5.seconds))
  }

}