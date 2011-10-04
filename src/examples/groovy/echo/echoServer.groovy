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

println("Creating echo server")

/* I think I like the uncommented style better since
 * it more closely matches the style used by NetClient
nodex {
  netServer (
    onConnect: { connection ->
      connection.dataHandler { data ->
        connection << data
      }
    }
  ).listen(8080)
}
*/
nodex {
  netServer().listen(8080) { connection ->
		connection.dataHandler { data ->
			connection << data
		}
	}
}


println("Hit enter to exit")
System.in.read()
