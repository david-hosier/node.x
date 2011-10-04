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
require 'addons/redis.rb'
require 'utils'
include Nodex

# We don't test everything since that is done at the Java level.
# We try and test most types of methods though
# @author {http://tfox.org Tim Fox}
class RedisTest < Test::Unit::TestCase

  KEY1 = Buffer.create("key1")
  KEY2 = Buffer.create("key2")
  KEY3 = Buffer.create("key3")

  VAL1 = Buffer.create("val1")
  VAL2 = Buffer.create("val2")
  VAL3 = Buffer.create("val3")

  def test_method_with_buffer_arg

    latch = Utils::Latch.new(1)

    Nodex::go {
      pool = RedisPool.new
      conn = pool.connection
      comp = Composer.new
      comp.series(conn.set(KEY1, VAL1))
      future1 = comp.series(conn.get(KEY1))
      comp.series{ assert(Utils::buffers_equal(VAL1, future1.result)) }
      comp.series(conn.close_deferred)
      comp.series{latch.countdown}
      comp.execute
    }

    assert(latch.await(5))

  end

  def test_method_with_buffer_array_arg

    latch = Utils::Latch.new(1)

    Nodex::go {
      pool = RedisPool.new
      conn = pool.connection
      comp = Composer.new
      comp.series(conn.r_push(KEY1, VAL1, VAL2, VAL3))
      future1 = comp.series(conn.r_pop(KEY1))
      future2 = comp.parallel(conn.r_pop(KEY1))
      future3 = comp.parallel(conn.r_pop(KEY1))
      comp.series{
        assert(Utils::buffers_equal(VAL1, future1.result))
        assert(Utils::buffers_equal(VAL2, future2.result))
        assert(Utils::buffers_equal(VAL3, future3.result))
      }
      comp.series(conn.close_deferred)
      comp.series{latch.countdown}
      comp.execute
    }

    assert(latch.await(5))

  end

  def test_method_with_buffer_array_ret

    latch = Utils::Latch.new(1)

    Nodex::go {
      pool = RedisPool.new
      conn = pool.connection
      comp = Composer.new
      comp.series(conn.r_push(KEY1, VAL1, VAL2, VAL3))
      future = comp.series(conn.l_range(KEY1, 0, -1))
      comp.series{
        assert_buff_arrays_equals([VAL1, VAL2, VAL3], future.result)
      }
      comp.series(conn.close_deferred)
      comp.series{latch.countdown}
      comp.execute
    }

    assert(latch.await(5))

  end

  def assert_buff_arrays_equals(expected, actual)
    assert expected.size == actual.size
    for i in 0...expected.size - 1
      assert(Utils::buffers_equal(expected[i], actual[i]))
    end
  end
end