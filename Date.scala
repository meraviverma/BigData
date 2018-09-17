import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{ DateType, IntegerType }
import org.apache.spark.sql.functions._
object Date {
  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "D:\\workspace_scala\\Practice\\hadoop")
    val spark = SparkSession
      .builder().master("local")
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
    import spark.implicits._
    val df = spark.read.option("header", "true").csv("D:\\workspace_scala\\Practice\\date.csv")
    //df.show()
    df.createOrReplaceTempView("people")
    val sel = spark.sql("select from_unixtime(Unixdate, 'yyyy-MM-dd') as Date from people")
    sel.show()
    sel.write.mode("append").option("header", "true").csv("D:\\workspace_scala\\Practice\\output\\Monthly_date.csv")
    val yearDF = sel.withColumn("Year", year(sel.col("Date"))).withColumn("Mon", month(sel.col("Date"))).
      withColumn("Day", dayofmonth(sel.col("Date")))
    yearDF.show()
    val first_week = yearDF.select("Date").filter($"Day" < 8)
    first_week.show()
    val second_week = yearDF.select("Date").filter($"Day" > 7 and $"Day" < 16)
    second_week.show()
    val third_week = yearDF.select("Date").filter($"Day" > 15 and $"Day" < 24)
    third_week.show()
    val forth_week = yearDF.select("Date").filter($"Day" > 23)
    forth_week.show()
    first_week.write.mode("append").option("header", "true").csv("D:\\workspace_scala\\Practice\\output\\first_week.csv")
    second_week.write.mode("append").option("header", "true").csv("D:\\workspace_scala\\Practice\\output\\second_week.csv")
    third_week.write.mode("append").option("header", "true").csv("D:\\workspace_scala\\Practice\\output\\third_week.csv")
    forth_week.write.mode("append").option("header", "true").csv("D:\\workspace_scala\\Practice\\output\\forth_week.csv")
  

    
    
    
    

}

} 
