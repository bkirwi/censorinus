package github.gphat.censorinus

import org.scalatest._
import org.scalatest.concurrent.Eventually
import github.gphat.censorinus.statsd.Encoder

class SynchronySpec extends FlatSpec with Matchers with Eventually {

  "Client" should "deal with gauges" in {
    val s = new TestSender(1)
    val client = new Client(encoder = Encoder, sender = s)

    // Queue up a message in the sender to ensure we can't publish yet.
    s.buffer.offer("BOO!")
    s.buffer.size should be (1)

    client.enqueue(Metric(name = "foobar", value = "1.0", metricType = "g"))
    s.buffer.size should be (1) // New metric won't be there yet
    s.awaitMessage() should be ("BOO!")
    s.awaitMessage() should include ("foobar")
    client.shutdown
  }

  it should "be synchronous" in {
    val s = new TestSender()
    val client = new Client(encoder = Encoder, sender = s, asynchronous = false)

    client.enqueue(Metric(name = "foobar", value = "1.0", metricType = "g"))
    val m = s.buffer.poll()
    m should include ("foobar")
    client.shutdown
  }
}
