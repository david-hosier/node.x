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

package org.nodex.groovy.core.buffer

import org.nodex.java.core.buffer.Buffer

public class Buffer {

  private static @Delegate org.nodex.java.core.buffer.Buffer jBuffer

  /**
   * Creates a nodex Java Buffer from either a String or a GString and returns it.
   */
  def static create(str, encoding = "UTF-8") {
    org.nodex.java.core.buffer.Buffer.create(new String(str), encoding)
  }
  
}