import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{ DateType, IntegerType }
import org.apache.spark.sql.functions._
import org.apache.avro.SchemaBuilder
import com.databricks.spark.avro._
import org.apache.avro.Schema
import org.apache.spark.sql.types._
import scala.util.Try
import scala.util.Try
import com.typesafe.config.ConfigFactory
object avroread {

  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "D:\\workspace_scala\\Practice\\hadoop")
    val spark = SparkSession
      .builder().master("local")
      .appName("Spark SQL avro ")
      .config("spark.some.config.option", "avro-convert")
      .getOrCreate()

    import spark.implicits._
    var value = ConfigFactory.load().getString("file1" + "wcr1" +"-turbinefact-01-2018.avro" )

    val df = spark.read.avro(value)
    df.printSchema()
    df.show()

    df.createOrReplaceTempView("Avro_Table_view")

    val sel = spark.sql("select PLANTSK,TURBINESK,DATASTATUSSK,DATESK,from_unixtime(CAST((UTCDATETIME/1000) as BIGINT), 'YYYY-MM-dd HH:mm:ss') as UTCDATETIME,LOADUTCDATETIME,DRIVERSK,EDITABLEEVENTTYPESK,EDITABLEFPLECATEGORYSK,EDITABLEGADSCATEGORYSK,EDITABLEMECHAVAILCATEGORYSK,EDITABLETURBSPLYAGMTCATEGORYSK,EVENTSK,LOCALDATETIME,AIRDENSITY,ALTERNATEAIRDENSITY,ALTERNATEAIRDENSITYSOURCE,ENERGYNET,EXPECTEDENERGY,LOSTENERGY,MEANPOWER,POWERSETPOINT,DOWNTIMESECONDS,ONLINESECONDS,TURBINEOKSECONDS,MEANWINDSPEED,NORMALIZEDWINDSPEED,ALTERNATEMEANWINDSPEED,ALTERNATEMEANWINDSPEEDSOURCE,ISACTIVE,DELAYSECONDS,CURTAILEDENERGY,DEVIATEDENERGY,EXPECTEDPARKLIMIT,FILTEREDLOSTENERGY,NEGATIVEDEVIATEDENERGY,PREDICTEDENERGY,FILTEREDPREDICTEDENERGY,OPNORMALIZEDWINDSPEED,FILTEREDENERGYNET,WLSTATUSFLAG,WBIEXPECTEDENERGY,WBILOSTENERGY,SCADAMEANWINDSPEED,WBIEVENTID,ISTURBINEACTIVE,LOSS_BUCKET,LOSS_GROUP,SITEMWCURTSETPOINT,MISCSK,METERSK,REVRATETARGET,TURBINEDERATEPERCENT,RAWENERGYNET,BACKFILLENERGYNET,SUBGROUPSK,XTIMESK,WLSUBSTATUSFLAG,MAXIMO_ID from Avro_Table_view")
     // sel.printSchema()
     sel.show()

    //  sel.write.mode("append").option("header", "true").csv("D:\\workspace_scala\\Practice\\output\\Monthly_date.csv")

    val yearDF = sel.withColumn("Day", dayofmonth(sel.col("UTCDATETIME")))

    //sel.withColumn("Year", year(sel.col("UTCDATETIME"))).withColumn("Mon", month(sel.col("UTCDATETIME"))).
   // yearDF.show()

    val first_week = yearDF.select("*").filter($"Day" < 8).drop("Day")
    val fnlFirstWeek = first_week.withColumn("UTCDATETIME", unix_timestamp(first_week.col("UTCDATETIME")))
    fnlFirstWeek.show()

    val second_week = yearDF.select("*").filter($"Day" > 7 and $"Day" < 16).drop("Day")
    val fnlSecondWeek = second_week.withColumn("UTCDATETIME", unix_timestamp(second_week.col("UTCDATETIME")))
    fnlSecondWeek.show()

    val third_week = yearDF.select("*").filter($"Day" > 15 and $"Day" < 24).drop("Day")
    val fnlThirdWeek = third_week.withColumn("UTCDATETIME", unix_timestamp(third_week.col("UTCDATETIME")))
    fnlThirdWeek.show()

    val forth_week = yearDF.select("*").filter($"Day" > 23).drop("Day")
    val fnlFourthweek = forth_week.withColumn("UTCDATETIME", unix_timestamp(forth_week.col("UTCDATETIME")))
    fnlFourthweek.show()

    fnlFirstWeek.write.format("com.databricks.spark.avro").mode("append").avro("D:\\workspace_scala\\Practice\\output\\first_week.avro")
    fnlSecondWeek.write.format("com.databricks.spark.avro").mode("append").avro("D:\\workspace_scala\\Practice\\output\\second_week.avro")
    fnlThirdWeek.write.format("com.databricks.spark.avro").mode("append").avro("D:\\workspace_scala\\Practice\\output\\third_week.avro")
    forth_week.write.format("com.databricks.spark.avro").mode("append").avro("D:\\workspace_scala\\Practice\\output\\forth_week.avro")
  }
}