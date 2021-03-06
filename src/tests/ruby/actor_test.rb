# Copyright 2011 VMware, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

require 'test/unit'
require 'nodex'
require 'utils'
include Nodex

class ActorTest < Test::Unit::TestCase

  def test_actor

    latch1 = Utils::Latch.new(2)

    latch2 = Utils::Latch.new(1)

    key1 = "actor1"
    key2 = "actor2"

    shared_hash = SharedData::get_hash("foo")

    msg1 = "hello from outer"
    msg2 = "hello from actor1"

    Nodex::go {
      id1 = Nodex::register_handler { |msg|
        assert(msg1 == msg)
        id2 = shared_hash[key2]
        Nodex::send_to_handler(id2, msg2)
      }
      shared_hash[key1] = id1
      latch1.countdown
    }

    Nodex::go {
      id2 = Nodex::register_handler { |msg|
        assert(msg2 == msg)
        Nodex::unregister_handler(id2)
        latch2.countdown
      }
      shared_hash[key2] = id2
      latch1.countdown
    }

    assert(latch1.await(5))

    Nodex::go {
      id1 = shared_hash[key1]
      Nodex::send_to_handler(id1, msg1)
    }

    assert(latch2.await(5))

  end
end