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

class NetSocket {

  @Delegate org.nodex.java.core.net.NetSocket jSocket

  NetSocket(jSocket) {
    this.jSocket = jSocket
  }

  /*
   * The java NetSocket class does not have getWriteHandlerID()
   * method, so it is not added to this class via the @Delegate
   */
  def getWriteHandlerID() {
    return jSocket.writeHandlerID
  }

  def dataHandler(hndlr) {
    jSocket.dataHandler(wrapHandler(hndlr))
  }

  def closedHandler(hndlr) {
    jSocket.closedHandler(wrapHandler(hndlr))
  }

  /*
   * This allows us to use the construct:
   *    socket << data << data << data
   *
   * By returning "this", we can chain the calls
   */
  def leftShift(buff) {
  	if (buff instanceof GString) { write(buff.toString()) }
  	else { write(buff) }
    return this
  }

  // Wrap the Groovy closure in a an anonymous class so the java core can call it
  def wrapHandler(hndlr) {
    return new org.nodex.java.core.Handler() {
	  void handle(data) {
	    hndlr.call(data)
	  }
    }
  }

}
