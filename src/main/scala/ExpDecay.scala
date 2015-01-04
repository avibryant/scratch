import com.twitter.algebird._

object ExpDecay {
  val dvMonoid = new DecayedValueMonoid(0.01)

  def main(args: Array[String]) {
    val hl1 = args(0).toDouble
    val hl2 = args(1).toDouble

    var dv1 = DecayedValue.zero
    var dv2 = DecayedValue.zero

    var df1 = DecayedValue.zero
    var df2 = DecayedValue.zero

    io.Source.stdin.getLines.zipWithIndex.foreach {
      case (line, i) =>
        val value = line.toDouble
        val time = i.toDouble
        dv1 = dvMonoid.plus(dv1, DecayedValue.build(value, time, hl1))
        dv2 = dvMonoid.plus(dv2, DecayedValue.build(value, time, hl2))
        df1 = dvMonoid.plus(df1, DecayedValue.build(1.0, time, hl1))
        df2 = dvMonoid.plus(df2, DecayedValue.build(1.0, time, hl2))
        val v1 = dvMonoid.valueAsOf(dv1, hl1, time)
        val v2 = dvMonoid.valueAsOf(dv2, hl2, time)
        val g1 = dvMonoid.valueAsOf(df1, hl1, time)
        val g2 = dvMonoid.valueAsOf(df2, hl2, time)
        val f1 = (hl1 - (hl1 * math.pow(2, -time / hl1))) / math.log(2)
        val f2 = (hl2 - (hl2 * math.pow(2, -time / hl2))) / math.log(2)
        val ff1 = hl1 / math.log(2)
        val ff2 = hl2 / math.log(2)
        val ewma1 = v1 / g1
        val ewma2 = v2 / g2
        val ratio = (ewma1 / ewma2) - 1.0
        val diff = (ewma1 - ewma2)
        println(List(time, value, ewma1, ewma2, diff * 4).mkString("\t"))
    }
  }
}

/*
(v2 + (x*f2)) / f2 = ewma1
v2/f2 + x = ewma1
ewma1 * f2 - v2 = x/f2
f2 * f2 * ewma1 - v2*f2 = x
*/
