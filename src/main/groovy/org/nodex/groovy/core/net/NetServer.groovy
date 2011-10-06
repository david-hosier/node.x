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

  org.nodex.java.core.net.NetServer jDelegate
  private NetServer me

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

  def setClientAuthRequired(required) {
    jDelegate.setClientAuthRequired(required)
    return this
  }
  
  def listen(port, host = "0.0.0.0", handler) {
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
