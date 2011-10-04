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

package org.nodex.groovy

import org.nodex.groovy.core.Nodex
import org.nodex.groovy.core.net.*
import org.nodex.groovy.core.buffer.Buffer

abstract class NodexScript extends Script {

	Nodex defaultNodex = new Nodex()
	
	static {
		String.metaClass.buffer = { encoding -> Buffer.create(delegate,encoding) }
		String.metaClass.buffer = { -> Buffer.create(delegate) }
		GString.metaClass.buffer = { encoding -> Buffer.create(delegate,encoding) }
		GString.metaClass.buffer = { -> Buffer.create(delegate) }
	}

  def nodex(closure) {
    Nodex.go closure
  }
  
  def sendToHandler(actorID, message) {
    defaultNodex.sendToHandler(actorID, message)
  }
  
  def registerHandler(handler) {
  	defaultNodex.registerHandler(handler)
  }
  
  def unregisterHandler(id) {
		defaultNodex.unregisterHandler(id)
	}
  
  def netServer(args) {
    new NetServer(args)
  }
  
  def netClient() {
  	new NetClient()
  }
}