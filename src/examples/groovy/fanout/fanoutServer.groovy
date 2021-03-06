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

import org.nodex.java.core.shared.SharedData
import org.nodex.groovy.core.Nodex
import org.nodex.groovy.core.net.NetServer

println "Creating fanout server"

Nodex.go {
  def connections = SharedData.getSet("conns")
  new NetServer().listen(8080) { connection ->
    println "Adding handler: ${connection.writeHandlerID}"
    connections << connection.writeHandlerID

    connection.dataHandler { data ->
      if (data) {
        println "Got data: ${data}"
        connections.each { id ->
          Nodex.sendToHandler(id, data)
        }
      }
    }
    
    connection.closedHandler {
      println "Removing handler: ${connection.writeHandlerID}"
      connections -= connection.writeHandlerID
    }
  }
}

println "Hit enter to exit"
System.in.read()

