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
 * Mixin module that provides all the common SSL params that can be set.
 */
public class SSLSupport {

  /**
   * Set whether the server or client will use SSL.
   * @param ssl If true then ssl will be used.
   * @return self So multiple invocations can be chained.
   */
  def setSSL(ssl) {
    jDelegate.setSSL(ssl)
    return me
  }
  
  /**
   * Set the path to the SSL key store. This method should only be used with the client/server in SSL mode, i.e. after {#ssl=}
   * has been set to true.
   * The SSL key store is a standard Java Key Store, and should contain the client/server certificate. For a client, it's only necessary to supply
   * a client key store if the server requires client authentication via client certificates.
   * @param [String] val. The path to the key store
   * @return [] self. So multiple invocations can be chained.
   */
  def setKeyStorePath(path) {
    jDelegate.setKeyStorePath(path)
    return me
  }
  
  def setKeyStorePassword(password) {
    jDelegate.setKeyStorePassword(password)
    return me
  }
  
  def setTrustStorePath(path) {
    jDelegate.setTrustStorePath(path)
    return me
  }
  
  def setTrustStorePassword(password) {
    jDelegate.setTrustStorePassword(password)
    return me
  }

}