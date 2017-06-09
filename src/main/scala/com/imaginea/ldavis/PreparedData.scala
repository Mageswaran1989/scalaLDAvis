package com.imaginea.ldavis

import java.io.{File, PrintWriter}

import org.apache.commons.io.FileUtils
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import spray.json._

/**
  * Created by mageswarand on 5/6/17.
  */

case class JsonData(mdsDat: JsValue, tinfo: JsValue, `token.table`: JsValue,
                    R: Double, `lambda.step`: Double, `plot.opts`: Map[String, String],
                   `topic.order`: Array[Int])

class PreparedData(topicCoordinates: Dataset[TopicCoordinates], topicInfo: DataFrame,
                   tokenTable: DataFrame, R: Int, lambdaStep: Double,
                   plotOpts: Map[String, String], topicOrder:Array[Int]) {

  val mdsDat = Utils.DfToJson.topicCoordinatesToJson(topicCoordinates)
  val tinfo = Utils.DfToJson.topicInfoToJson(topicInfo)
  val tokenTableJson = Utils.DfToJson.tokenTableToJson(tokenTable)

  case class Rinfo(R: Int)
  case class TOinfo(topicOrder: Array[Int])
  case class LambdaStepInfo(lambda_step: Double)

  object customJsonProtocol extends DefaultJsonProtocol {
    implicit val protocolRinfoJsonProtocol = jsonFormat1(Rinfo)
    implicit val protocolTOinfo = jsonFormat1(TOinfo)
    implicit val protocolLSInfo = jsonFormat1(LambdaStepInfo)
    implicit val finalFormat = jsonFormat7(JsonData)
  }
  import customJsonProtocol._

  val data = JsonData(mdsDat , tinfo , tokenTableJson , R , lambdaStep , plotOpts  , topicOrder )


  def exportTo(path: String = "/tmp/scalaLDAvis/") = {
    new PrintWriter("/tmp/scalaLDAvis.json") { write(data.toJson.prettyPrint); close }
    new File(path).mkdirs()
    FileUtils.copyFile(new File(getClass.getResource("dataset/javascript/index.html").getFile), new File(path))
  }


}


object PreparedData {
  def apply(topicCoordinates: Dataset[TopicCoordinates], topicInfo: DataFrame,
            tokenTable: DataFrame, R: Int, lambdaStep: Double,
            plotOpts: Map[String, String], topicOrder:Array[Int]) = {
     new PreparedData(topicCoordinates: Dataset[TopicCoordinates], topicInfo: DataFrame,
       tokenTable: DataFrame, R: Int, lambdaStep: Double,
       plotOpts: Map[String, String], topicOrder:Array[Int])
  }
}