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
 * <p>
 * <b>IMPORTANT NOTE:</b> The methods of this class are designed to return a handle back to the
 * class which contain this as a Mixin. However, there is no support in Groovy at this
 * time that can be used to make that happen. For example, returning 'this' will attempt
 * to return the instance of the script that is currently being executed, not the instance
 * of the class on which the Mixin annotation exists.
 * </p>
 * <p>
 * This has been worked around for now by simply returning a variable named 'me' in each
 * method. This requires the classes using this class as a mixin to provide a private
 * class-level property named me and which has 'this' assigned to it during construction of
 * the instance. Hopefully Groovy will implement a proper way to accomplish the needs in
 * the near future.
 * </p>
 */
public class SSLSupport {

  /**
   * Set whether the server or client will use SSL.
   * @param ssl If true then ssl will be used.
   * @return self so multiple invocations can be chained.
   */
  def setSSL(boolean ssl) {
    jDelegate.setSSL(ssl)
    return me
  }
  
  /**
   * Set the path to the SSL key store. This method should only be used in SSL mode, i.e. after {@link #setSSL(boolean)}
   * has been set to {@code true}.<p>
   * The SSL key store is a standard Java Key Store, and, if on the server side will contain the server certificate. If
   * on the client side it will contain the client certificate. Client certificates are only required if the server
   * requests client authentication.<p>
   * @return self so multiple invocations can be chained.
   */
  def setKeyStorePath(path) {
    jDelegate.setKeyStorePath(path)
    return me
  }
  
  /**
   * Set the password for the SSL key store. This method should only be used in SSL mode, i.e. after {@link #setSSL(boolean)}
   * has been set to {@code true}.<p>
   * @return self so multiple invocations can be chained.
   */
  def setKeyStorePassword(password) {
    jDelegate.setKeyStorePassword(password)
    return me
  }
  
  /**
   * Set the path to the SSL trust store. This method should only be used in SSL mode, i.e. after {@link #setSSL(boolean)}
   * has been set to {@code true}.<p>
   * The trust store is a standard Java Key Store, and, if on the server side it should contain the certificates of
   * any clients that the server trusts - this is only necessary if client authentication is enabled. If on the
   * client side, it should contain the certificates of any servers the client trusts.
   * If you wish the client to trust all server certificates you can use the {@link NetClientBase#setTrustAll(boolean)} method.<p>
   * @return self so multiple invocations can be chained.
   */
  def setTrustStorePath(path) {
    jDelegate.setTrustStorePath(path)
    return me
  }
  
  /**
   * Set the password for the SSL trust store. This method should only be used in SSL mode, i.e. after {@link #setSSL(boolean)}
   * has been set to {@code true}.<p>
   * @return self so multiple invocations can be chained.
   */
  def setTrustStorePassword(password) {
    jDelegate.setTrustStorePassword(password)
    return me
  }

}