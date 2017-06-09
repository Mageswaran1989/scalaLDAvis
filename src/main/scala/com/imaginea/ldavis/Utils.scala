package com.imaginea.ldavis

import org.apache.spark.sql.{DataFrame, Dataset}
import scala.reflect.runtime.universe._
import spray.json._
import spray.json.DefaultJsonProtocol._
/**
  * Created by mageswarand on 6/6/17.
  */
object Utils {


  object DfToJson {

    //https://medium.com/@sinisalouc/overcoming-type-erasure-in-scala-8f2422070d20
    def classAccessors[T: TypeTag]: List[(String, String)] = typeOf[T].members.collect {
      case m: MethodSymbol if m.isCaseAccessor => { //If the pmember is  constructor paramaeter
        (m.name.toString, m.returnType.typeSymbol.name.toString)
      }
    }.toList

    def tokenTableToJson(df: DataFrame) = {
      //USe reflection to get the column names w.r.t to df/case class and use it create
      import df.sparkSession.implicits._

      val data = ZippedTokenTable(TermId= Array(0),
      Topic = df.select("Topic").as[Int].collect(),
      Freq = df.select("Freq").as[Double].collect(),
      Term = df.select("Term").as[String].collect() )

      object MyJsonProtocol extends DefaultJsonProtocol {
        implicit val zippedTopicTopTermRowsFormat = jsonFormat4(ZippedTokenTable)
      }

      import MyJsonProtocol._

      data.toJson

    }

    def topicInfoToJson(df: DataFrame) = {
      //Establish a relation between given DataFrame and the case class to determine the column names and their types
      //Method 1: Convert DF to RDD and iterate to create a case class and use it to create the zipped version of case class
      //Methos 2: Select the column by name and create a zipped case class  out of it
      import df.sparkSession.implicits._
      println(df.select("Term").as[String].collect().foreach(println))
      val data = ZippedTopicTopTermRows(Term=df.select("Term").as[String].collect(),
        Freq = df.select("Freq").as[Double].collect(),
        Total=df.select("Total").as[Double].collect(),
        Category =df.select("Category").as[String].collect() ,
        loglift = df.select("loglift").as[Double].collect(),
        logprob =df.select("logprob").as[Double].collect() )

      object MyJsonProtocol extends DefaultJsonProtocol {
        implicit val zippedTopicTopTermRowsFormat = jsonFormat6(ZippedTopicTopTermRows)
      }

      import MyJsonProtocol._

      data.toJson
    }

    def topicCoordinatesToJson(df: Dataset[TopicCoordinates]) = {
      //Establish a relation between given DataFrame and the case class to determine the column names and their types
      //Method 1: Convert DF to RDD and iterate to create a case class and use it to create the zipped version of case class
      //Methos 2: Select the column by name and create a zipped case class  out of it


      //https://stackoverflow.com/questions/30758105/convert-rdd-to-json-object
      //    df.select($"")
      import df.sparkSession.implicits._

      val data = ZippedTopicCoordinates(x=df.select("x").as[Double].collect(),
        y=df.select("y").as[Double].collect(),
        topics = df.select("topics").as[Int].collect(),
        cluster = df.select("cluster").as[Int].collect(),
        Freq = df.select("Freq").as[Double].collect()
      )

      object MyJsonProtocol extends DefaultJsonProtocol {
        implicit val zippedTopicCoordinatesFormat = jsonFormat5(ZippedTopicCoordinates)
      }

      import MyJsonProtocol._
      data.toJson
    }
  }
}
