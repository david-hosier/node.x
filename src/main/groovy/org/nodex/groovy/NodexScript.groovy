package org.nodex.groovy

import org.nodex.groovy.core.Nodex

abstract class NodexScript extends Script {
	
  def nodex(closure) {
    Nodex.go closure
  }
  
  def sendToHandler(id, data) {
    Nodex.j_instance.sendToHandler(id, data)
  }
  
}