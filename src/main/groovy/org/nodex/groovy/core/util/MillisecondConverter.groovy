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

package org.nodex.groovy.core.util

import java.util.concurrent.TimeUnit

class MillisecondConverter {

  static long getSeconds(def val) {
    TimeUnit.MILLISECONDS.convert(new Long(val), TimeUnit.SECONDS)
  }

  static long getSecond(def val) {
    val > 1 ? getSeconds(val) : (long)1000
  }

  static long getMinutes(def val) {
    TimeUnit.MILLISECONDS.convert(new Long(val), TimeUnit.MINUTES)
  }

  static long getMinute(def val) {
    getMinutes(val)
  }

  static long getHours(def val) {
    TimeUnit.MILLISECONDS.convert(new Long(val), TimeUnit.HOURS)
  }

  static long getHour(def val) {
    getHours(val)
  }

}