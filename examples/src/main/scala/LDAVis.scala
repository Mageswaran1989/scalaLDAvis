package main.scala

import com.imaginea.ldavis.ScalaLDAvisBuilder

/**
  * Created by mageswarand on 27/6/17.
  */
object LDAVis {
  def main(args: Array[String]): Unit = {

    val sparkLDAvis = new ScalaLDAvisBuilder()
      .withTransformedDfPath("/tmp/scalaLDAvis/model/transformedDF") //features, topicDistribution(num_docs x K)
      .withLDAModelPath("/tmp/scalaLDAvis/model/spark-lda") //topic_term_dist [k topics x V words]
      .withCVModelPath("/tmp/scalaLDAvis/model/cv-model")
      .withEnableDebug(true)
      .build

    sparkLDAvis.prepareLDAVisData("/tmp/scalaLDAvis/scalaLDAVis")

  }
}
