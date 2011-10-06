import java.util.concurrent.CountDownLatch
import org.nodex.java.core.buffer.Buffer

class Utils {

  static CountDownLatch latch(count = 1) {
    new CountDownLatch(count)
  }

  static boolean buffersEqual(buff1, buff2) {
    org.nodex.tests.Utils.buffersEqual(buff1, buff2)
  }
  
  static def genBuffer(size) {
    org.nodex.tests.Utils.generateRandomBuffer(size)
  }
}