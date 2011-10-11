import java.util.concurrent.TimeUnit
import org.nodex.java.core.buffer.Buffer

class Utils {

  static Latch latch(count = 1) {
    new Latch(count)
  }

  static boolean buffersEqual(buff1, buff2) {
    org.nodex.tests.Utils.buffersEqual(buff1, buff2)
  }
  
  static def genBuffer(size) {
    org.nodex.tests.Utils.generateRandomBuffer(size)
  }
}