package com.imaginea.ldavis

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.clustering.LDAModel
import org.apache.spark.ml.feature.CountVectorizerModel
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import scala.reflect.runtime.universe._
//import scala.language.implicitConversions

/**
  * Created by mageswarand on 5/6/17.
  */

abstract class LDAvis {
  /**
    * Creates the directory if not existing and copy the necessary files for
    * visualization.
    *
    * Open the 'index.html' in Firefox for the visualization to work seamlessly!
    * @param directory Where you wanted to store the visualizations
    * @param lambdaStep
    * @param plotOpts
    * @param R Number of topics to be shown on the UI. Recomended is 20 to 50
    */
  def prepareLDAVisData(directory: String,
                        lambdaStep: Double = 0.01,
                        plotOpts: Map[String, String] = Map("xlab" -> "PC1", "ylab" -> "PC2"),
                        R:Int =30)
}

case class LDAVisData()

abstract class LDAvisBuilder {

  //Spark Specific
  var spark: Option[SparkSession] = None
  def withSparkSession(spark: SparkSession): LDAvisBuilder

  //fitted or transformed or trained DataFrame that has
  // 'topicDistribution' or equivalent column created with LDA transformer
  var transformedDfPath: Option[String] = None
  var transformedDf: Option[DataFrame] = None
  var ldaOutCol: Option[String] = None //Retreived from LDA model
  def withTransformedDfPath(path: String): LDAvisBuilder
  def withTransformedDf(df: DataFrame): LDAvisBuilder

  //LDA Model - Runtime or from stored model
  var ldaModelPath: Option[String] = None
  var ldaModel: Option[LDAModel] = None
  def withLDAModel(model: LDAModel): LDAvisBuilder
  def withLDAPath(path: String): LDAvisBuilder

  //CountVectorizer Model - Runtime or from stored model
  var cvModelPath: Option[String] = None
  var cvModel: Option[CountVectorizerModel] = None
  var cvOutCol: Option[String] = None //Retreived from CV model
  def withCVModel(model: CountVectorizerModel): LDAvisBuilder
  def withCVPath(path: String): LDAvisBuilder

  //Vocabulary - Runtime or from stored model
  var vocabDFPath: Option[String] = None
  var vocab: Array[String] = Array()
  var vocabOutCol: Option[String] = None
  def withVocab(words: Array[String], vocabOutCol: String): LDAvisBuilder
  def withVocabDfPath(path: String, vocabOutCol: String): LDAvisBuilder


  //LDA pipeline runtime
  var ldaPipeline: Option[Pipeline] = None
  def withLDAPipeline(pipeline: Pipeline): LDAvisBuilder

  def build: LDAvis
}

//--------------------------------------------------------------------------------------
//TODO find a better way to convert the columns of arrays to DataFrame
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
