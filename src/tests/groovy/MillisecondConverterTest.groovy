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

import groovy.util.GroovyTestCase
import org.nodex.groovy.core.Nodex

class MillisecondConverterTest extends GroovyTestCase {

  void testSeconds() {
    Nodex.go {
			assert(1000 == 1.second)
			assert(2000 == 2.second)
			assert(2000 == 2.seconds)
    }
  }

  void testMinutes() {
    Nodex.go {
      assert(60000 == 1.minute)
      assert(120000 == 2.minutes)
      assert(120000 == 2.minute)
    }
  }

  void testHours() {
    Nodex.go {
      assert(3600000 == 1.hour)
      assert(7200000 == 2.hours)
      assert(7200000 == 2.hour)
    }
  }
}