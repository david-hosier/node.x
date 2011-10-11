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

package org.nodex.groovy.core.net

import org.nodex.java.core.net.NetServer

/**
 * <p>Encapsulates a server that understands TCP or SSL.</p>
 *
 * <p>
 * Instances of this class can only be used from the event loop that created it. When connections are accepted by the server
 * they are supplied to the user in the form of a {@link org.nodex.java.core.net.NetSocket} 
 * instance that is passed via the Closure set using either the {@link #connectHandler(Closure)}
 * method or passed to the {@link #listen(def,def,Closure)} method.
 * </p>
 * <p>
 * Here is an example of creating a simple echo server:
 * <pre>
 * Nodex.go {
 *   new NetServer().listen(8080) { socket ->
 *     socket.dataHandler { data ->
 *       socket << data
 *     }
 *   }
 * }
 * </pre>
 * </p>
 *
 * @see org.nodex.groovy.core.net.TCPSupport
 * @see org.nodex.groovy.core.net.SSLSupport
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author <a href="http://github.com/david-hosier">David Hosier</a>
 */
@Mixin([TCPSupport, SSLSupport])
public class NetServer {

  private def jDelegate
  private NetServer me

  /**
   * Creates a new NetServer. All properties can be set using named parameters, which nicely
   * avoids having to call a bunch of setters. For example:
   * <pre>
      def serverArgs = [
        SSL: true, 
        keyStorePath: "../resources/keystores/server-keystore.jks",
        keyStorePassword: "wibble",
        trustStorePath: "../resources/keystores/server-truststore.jks",
        trustStorePassword: "wibble",
        clientAuthRequired: true
      ]

      def server = new NetServer(serverArgs)...
   * </pre>
   *  Most properties are supplied via the use of the {@link org.nodex.groovy.core.net.TCPSupport}
   *  and {@link org.nodex.groovy.core.net.SSLSupport} mixins. 
   */
  NetServer() {
    jDelegate = new org.nodex.java.core.net.NetServer()
    me = this
  }
  
  def connectHandler(Closure hndlr) {
    def jHandler = new org.nodex.java.core.Handler() {
      void handle(jSocket) {
        hndlr.call(new NetSocket(jSocket))
      }
    }
    jDelegate.connectHandler(jHandler)
    return this
  }

  /**
   * Set {@code required} to true if you want the server to request client authentication from any connecting clients. This
   * is an extra level of security in SSL, and requires clients to provide client certificates. Those certificates must be added
   * to the server trust store.
   * @return A reference to this, so multiple invocations can be chained together.
   */
  def setClientAuthRequired(boolean required) {
    jDelegate.setClientAuthRequired(required)
    return this
  }
  
  def listen(port, host = "0.0.0.0", Closure handler) {
    connectHandler(handler)
    jDelegate.listen(port, host)
    return this
  }
  
  void close(Closure hndlr) {
    def jHandler = null
    if (hndlr) {
      jHandler = new org.nodex.java.core.Handler() {
        void handle(nada) {
          hndlr.call()
        }
      }
    }
    jDelegate.close(jHandler)
  }
  
}
