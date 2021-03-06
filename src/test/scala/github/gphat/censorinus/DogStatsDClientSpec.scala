package github.gphat.censorinus

import org.scalatest._
import scala.collection.mutable.ArrayBuffer
import github.gphat.censorinus.statsd.Encoder

class DogStatsDClientSpec extends FlatSpec with Matchers with BeforeAndAfter {

  var client: DogStatsDClient = null

  before {
    client = new DogStatsDClient(prefix = "poop")
    // SOOOOOOOOoooooo hacky, but this will ensure the worker thread doesn't
    // steal our metrics before we can read them.
    client.shutdown
  }

  "DogStatsDClient" should "deal with gauges" in {
    client.gauge("foobar", value = 1.0, tags = Seq("foo:bar"))
    val m = client.queue.poll
    m.name should be ("poop.foobar")
    m.value should be ("1.00000000")
    m.metricType should be ("g")
    m.tags should be (Seq("foo:bar"))
  }

  it should "deal with counters" in {
    client.counter("foobar", 1.0, tags = Seq("foo:bar"))
    val m = client.queue.poll
    m.name should be ("poop.foobar")
    m.value should be ("1.00000000")
    m.metricType should be ("c")
    m.tags should be (Seq("foo:bar"))
  }

  it should "deal with increments" in {
    client.increment("foobar", tags = Seq("foo:bar"))
    val m = client.queue.poll
    m.name should be ("poop.foobar")
    m.value should be ("1.00000000")
    m.metricType should be ("c")
    m.tags should be (Seq("foo:bar"))
  }

  it should "deal with decrements" in {
    client.increment("foobar", tags = Seq("foo:bar"))
    val m = client.queue.poll
    m.name should be ("poop.foobar")
    m.value should be ("1.00000000")
    m.metricType should be ("c")
    m.tags should be (Seq("foo:bar"))
  }

  it should "deal with histograms" in {
    client.histogram("foobar", 1.0, tags = Seq("foo:bar"))
    val m = client.queue.poll
    m.name should be ("poop.foobar")
    m.value should be ("1.00000000")
    m.metricType should be ("h")
    m.tags should be (Seq("foo:bar"))
  }

  it should "deal with meters" in {
    client.meter("foobar", 1.0, tags = Seq("foo:bar"))
    val m = client.queue.poll
    m.name should be ("poop.foobar")
    m.value should be ("1.00000000")
    m.metricType should be ("m")
    m.tags should be (Seq("foo:bar"))
  }

  it should "deal with sets" in {
    client.set("foobar", "fart", tags = Seq("foo:bar"))
    val m = client.queue.poll
    m.name should be ("poop.foobar")
    m.value should be ("fart")
    m.metricType should be ("s")
    m.tags should be (Seq("foo:bar"))
  }

  it should "deal with big doubles" in {
    client.meter("foobar", 1.01010101010101010101)
    val m = client.queue.poll
    m.name should be ("poop.foobar")
    m.value should be ("1.01010101")
  }
}
