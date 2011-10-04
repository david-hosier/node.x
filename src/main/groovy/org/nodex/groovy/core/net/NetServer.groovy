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

@Mixin([TCPSupport, SSLSupport])
public class NetServer {

  @Delegate org.nodex.java.core.net.NetServer jDelegate

  NetServer() {
    jDelegate = new org.nodex.java.core.net.NetServer()
  }
  
  NetServer(args) {
    this()
    /*
     * This is the code used to specify the connectHandler as a named param
    if (args) {
			if (args.onConnect) {
				connectHandler(args.onConnect)
			}
		}
		*/
  }
  
  def connectHandler(Closure hndlr) {
    // Wrap the Groovy closure in a an anonymous class so the java core can call it
    def jHandler = new org.nodex.java.core.Handler() {
      void handle(jSocket) {
        hndlr.call(new NetSocket(jSocket))
      }
    }
    jDelegate.connectHandler(jHandler)
    return this
  }

  def setClientAuthRequired(required) {
    jDelegate.setClientAuthRequired(required)
    return this
  }
  
  def listen(port, host = "0.0.0.0", handler) {
  	connectHandler(handler)
    jDelegate.listen(port, host)
    return this
  }
  
  def close(Closure hndlr) {
    def jHandler = new org.nodex.java.core.Handler() {
      void handle(nada) {
        hndlr.call()
      }
    }
    jDelegate.close(jHandler)
  }
}
