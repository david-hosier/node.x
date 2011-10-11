import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class Latch {

  private final CountDownLatch latch
  Latch(count = 1) {
    latch = new CountDownLatch(count)
  }

  def countDown() { latch.countDown() }
  def await(millis) { latch.await(millis, TimeUnit.MILLISECONDS) }
  def getCount() { latch.count }
}