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

package org.nodex.groovy.core

class Nodex {

  static def jDelegate = org.nodex.java.core.Nodex.instance
  static {
    String.metaClass.buffer = { encoding -> org.nodex.groovy.core.buffer.Buffer.create(delegate,encoding) }
    String.metaClass.buffer = { -> org.nodex.groovy.core.buffer.Buffer.create(delegate) }
    GString.metaClass.buffer = { encoding -> org.nodex.groovy.core.buffer.Buffer.create(delegate,encoding) }
    GString.metaClass.buffer = { -> org.nodex.groovy.core.buffer.Buffer.create(delegate) }
  }
  
  /**
   * Run the specified Closure inside an event loop. 
   * An event loop will be picked by the system from all available loops.
   */
  static def go(closure) {
    jDelegate.go(new java.lang.Runnable() {
      public void run() {
        closure.call()
      }
    })
  }

  /**
   * Send a message to the handler with the specified {@code actorID}. This can be called from any event loop.
   * @return true of the message was successfully sent, or false if no such handler exists.
   */
  static def sendToHandler(actorID, message) {
    jDelegate.sendToHandler(actorID, message)
  }

  /**
   * Register a global handler with the system. The handler can be invoked by calling the {@link #sendToHandler}
   * method from any event loop. The handler will always be called on the event loop that invoked the {@code
   * registerHandler} method.
   * @param handler a Closure to register as a Handler
   * @return the unique ID of the handler. This is required when calling {@link #sendToHandler}.
   */
  static def registerHandler(handler) {
    def jHandler = new org.nodex.java.core.Handler() {
      void handle(data) {
        handler.call(data)
      }
    }
    jDelegate.registerHandler(jHandler)
  }

  /**
   * Unregister the handler with the specified {@code id}. This must be called from the same event loop that
   * registered the handler.
   * @return true if the handler was successfully unregistered, otherwise false if the handler cannot be found.
   */
  static def unregisterHandler(id) {
    jDelegate.unregisterHandler(id)
  }
}