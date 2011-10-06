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

import org.nodex.java.core.net.NetClient

@Mixin([TCPSupport, SSLSupport])
public class NetClient {

  org.nodex.java.core.net.NetClient jDelegate
  private NetClient me

  NetClient() {
    jDelegate = new org.nodex.java.core.net.NetClient()
    me = this
  }
  
  def setTrustAll(trustAll) {
    jDelegate.setTrustAll(trustAll)
    return this
  }
  
  def connect(port, host = "localhost", handler) {
    println "Making client connection to ${host}:${port}"
    def jHandler = new org.nodex.java.core.Handler() {
      void handle(jSocket) {
        handler.call(new NetSocket(jSocket))
      }
    }
    
    jDelegate.connect(port, host, jHandler)
  }
  
  def setReconnectAttempts(value) {
    jDelegate.setReconnectAttempts(value)
    return this
  }
  
  def setReconnectInterval(value) {
    jDelegate.setReconnectInterval(value)
    return this
  }
  
  void close() {
    jDelegate.close()
  }

  // Only reconnectAttempts and reconnectInterval are readable
  def getProperty(String name) {
    return jDelegate[name]
  }
}