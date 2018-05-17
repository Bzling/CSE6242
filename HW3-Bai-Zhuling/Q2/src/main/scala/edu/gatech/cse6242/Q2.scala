package edu.gatech.cse6242

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._

object Q2 {

	def main(args: Array[String]) {
    	val sc = new SparkContext(new SparkConf().setAppName("Q2"))
		val sqlContext = new SQLContext(sc)
		import sqlContext.implicits._

    	// read the file
    	val file = sc.textFile("hdfs://localhost:8020" + args(0))
       val outedge = file.map(_.split("\t")).map(p => (p(0).toInt, p(2).toInt*(-1))).toDF("src", "outweight")
       val inedge = file.map(_.split("\t")).map(p => (p(1).toInt, p(2).toInt)).toDF("tgt", "inweight")

       val totalout = outedge.filter("outweight <= -5").groupBy("src")
                         .agg(sum(outedge("outweight")))
                                                                           
       val totalin = inedge.filter("inweight >= 5").groupBy("tgt")
                       .agg(sum(inedge("inweight")))    
                       

       
val joinresult = totalout.join(totalin, totalout("src") === totalin("tgt"), "fullouter")

val newNames = Seq("src1", "outt", "tgt1", "inn")
val dfRenamed = joinresult.toDF(newNames: _*)	

val finalresult = dfRenamed.select(coalesce($"src1", $"tgt1").alias("aa"), col("outt"), col("inn"))


val newNamess = Seq("a", "b", "c")
val finalresultt = finalresult.toDF(newNamess: _*)

val finall = finalresultt.na.fill(0)

val resultt = finall.select($"a", $"b"+$"c")
resultt.map(x => x.mkString("\t")).saveAsTextFile("hdfs://localhost:8020" + args(1))
  	}
}
