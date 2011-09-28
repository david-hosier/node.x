package org.nodex.groovy

import org.nodex.groovy.core.Nodex
import org.nodex.groovy.core.net.*

abstract class NodexScript extends Script {
	
  def nodex(closure) {
    Nodex.go closure
  }
  
  def sendToHandler(id, data) {
    Nodex.j_instance.sendToHandler(id, data)
  }
  
  def netServer(args) {
    new NetServer(args)
  }
}