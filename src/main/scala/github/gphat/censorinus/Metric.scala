package github.gphat.censorinus

case class Metric(
  name: String,
  value: String,
  metricType: String,
  sampleRate: Double = 1.0,
  tags: Seq[String] = Seq.empty,
  unit: String = ""
)
