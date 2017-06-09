package com.imaginea.ldavis

import org.apache.spark.ml.clustering.LDAModel
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import scala.reflect.runtime.universe._
//import scala.language.implicitConversions

/**
  * Created by mageswarand on 5/6/17.
  */

abstract class LDAvis {
  def prepareLDAVisData(path: String,
                        lambdaStep: Double = 0.01,
                        plotOpts: Map[String, String] = Map("xlab" -> "PC1", "ylab" -> "PC2"),
                        R:Int =30)
}

case class LDAVisData()

abstract class LDAvisBuilder {

  //Common
  var vocabSize: Long
  def withVocabSize(size: Long): LDAvisBuilder

  //Spark Specific
  var spark: Option[SparkSession]
  def withSparkSession(spark: SparkSession): LDAvisBuilder

  var trainedDFPath: Option[String]
  def withTrainedDF(path: String): LDAvisBuilder

  var ldaModel: Option[LDAModel]
  def withLDAModel(model: LDAModel): LDAvisBuilder

  var ldaModelPath: Option[String]
  def withLDAPath(path: String): LDAvisBuilder

  var vocab: Array[String]
  def withVocab(words: Array[String]): LDAvisBuilder

  var vocabDFPath: Option[String]
  def withVocabDFPath(path: String): LDAvisBuilder

  var transformedDF: Option[DataFrame]
  def withTransformedDF(df: DataFrame): LDAvisBuilder

  def build: LDAvis
}

//--------------------------------------------------------------------------------------
case class DefaultTermInfo(Saliency: Double, Term: String,
                           Freq: Double, Total:Double, Category: String)

case class ZippedDefaultTermInfo(Saliency: Array[Double], Term: Array[String],
                                 Freq: Array[Double], Total: Array[Double], Category: Array[String]) {

  def toDefaultTermInfoArray(R:Int) = {
    assert((Saliency.length == Term.length &&
      Saliency.length == Freq.length &&
      Saliency.length == Total.length &&
      Saliency.length == Category.length),
      "Length of all arrays should be same!"
    )

    val length = Saliency.length
    // Rounding Freq and Total to integer values to match LDAvis code:
    (0 until length).map(i => DefaultTermInfo(Saliency(i), Term(i),
      Math.floor(Freq(i)), Math.floor(Total(i)), Category(i)))
  }
}

//--------------------------------------------------------------------------------------

case class TopicTopTermRow(Term: String, Freq: Double, Total: Double, Category: String,
                           loglift: Double, logprop: Double)

case class ZippedTopicTopTermRows(Term: Array[String], Freq: Array[Double], Total: Array[Double],
                                  logprob: Array[Double], loglift: Array[Double], Category: Array[String]) {
  def toTopicTopTermRow() = {

    assert((Term.length == Freq.length &&
      Term.length == Total.length &&
      Term.length == logprob.length &&
      Term.length == loglift.length &&
      Term.length == Category.length), "Length of all arrays should be same!" +
      Term.length + "," +
      Total.length + "," +  logprob.length + "," +  loglift.length + "," + Category.length
    )

    val length = Term.length
    (0 until length).map(i => TopicTopTermRow(Term(i), Freq(i), Total(i), Category(i), loglift(i), logprob(i)))
  }
}

case class ZippedTopicInfo(Category:Array[String],	Freq:Array[Double],	Term:Array[String],
                           Total:Array[Double],	loglift:Array[Double],	logprob:Array[Double])

//--------------------------------------------------------------------------------------
case class TokenTable(TermId: Int, Topic: Int, Freq: Double, Term: String)



case class ZippedTokenTable(TermId: Array[Int], Topic: Array[Int], Freq: Array[Double], Term: Array[String])

case class TopicCoordinates(x: Double, y:Double, topics: Int, cluster: Int, Freq: Double)

case class ZippedTopicCoordinates(x: Array[Double], y:Array[Double], topics: Array[Int], cluster: Array[Int], Freq: Array[Double])
