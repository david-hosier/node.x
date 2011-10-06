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

/**
 * Mixin module that provides all the common TCP params that can be set.
 */
public class TCPSupport {

  /**
   * Set the TCP send buffer size.
   * @param size The size in bytes.
   * @return A reference to self so invocations can be chained
   */
  def setSendBufferSize(size) {
    jDelegate.setSendBufferSize(size)
    return me
  }

  /**
   * Set the TCP receive buffer size.
   * @param size The size in bytes.
   * @return A reference to self so invocations can be chained
   */
  def setReceiveBufferSize(size) {
    jDelegate.setReceiveBufferSize(size)
    return me
  }
  
  /**
   * Set the TCP keep alive setting.
   * @param keepAlive If true, then TCP keep alive will be enabled.
   * @return A reference to self so invocations can be chained
   */
  def setTCPKeepAlive(keepAlive) {
    jDelegate.setTCPKeepAlive(keepAlive)
    return me
  }
  
  /**
   * Set the TCP reuse address setting.
   * @param reuse If true, then TCP reuse address will be enabled.
   * @return A reference to self so invocations can be chained
   */
  def setReuseAddress(reuse) {
    jDelegate.setReuseAddress(reuse)
    return me
  }
  
  /**
   * Set the TCP so linger setting.
   * @param soLinger If true, then TCP so linger will be enabled.
   * @return A reference to self so invocations can be chained
   */
  def setSoLinger(soLinger) {
    jDelegate.setSoLinger(soLinger)
    return me
  }
  
  /**
   * Set the TCP traffic class setting.
   * @param trafficClass The TCP traffic class setting.
   * @return A reference to self so invocations can be chained
   */
  def setTrafficClass(trafficClass) {
    jDelegate.setTrafficClass(trafficClass)
    return me
  }

}