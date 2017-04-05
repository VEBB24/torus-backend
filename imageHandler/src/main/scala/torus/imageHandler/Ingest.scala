package torus.imageHandler

import java.io.IOException

import geotrellis.proj4.{CRS, WebMercator}
import geotrellis.raster.{DoubleArrayTile, IntArrayTile, MultibandTile, Raster, RasterExtent}
import geotrellis.raster.resample.Bilinear
import org.apache.spark.{SparkConf, SparkContext}
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.file._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.index._
import geotrellis.spark.pyramid._
import geotrellis.spark.reproject._
import geotrellis.spark.tiling._
import geotrellis.spark.render._
import geotrellis.vector.{Extent, Point, ProjectedExtent}
import geotrellis.vectortile.protobuf.internal.vector_tile.Tile.Feature
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileStatus, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn


case class Learn(
                  ID: String,
                  X_OK: String,
                  Y_OK: String,
                  SOC_Hybrid: String,
                  CLA: String
                )

case class Info(
  lng: Double,
  lat: Double,
  value: Double,
  learn: Learn
)

object Ingest extends App {

  val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("Ingest")
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .config("spark.executor.memory", "3g")
    .config("spark.kryoserializer.buffer.max", "600mb")
    .getOrCreate()

  implicit val sc = spark.sparkContext


  val configuration: Configuration = new Configuration()
  configuration.set("fs.defaultFS", "hdfs://127.0.0.1:8020/")

  val rootPath: Path = new Path("/user/admin/boutespaul")
  val store: HadoopAttributeStore = HadoopAttributeStore(rootPath, configuration)
  val reader = HadoopLayerReader(rootPath)
  val writer = HadoopLayerWriter(rootPath, store)

  def process(implicit sc: SparkContext) {
    import spark.implicits._
    println("START")

    val ds: Dataset[Learn] = spark
      .read
      .option("header", true)
      .csv("hdfs://127.0.0.1:8020/user/admin/boutespaul/learn2016.csv")
      .as[Learn]

    val dsArray = ds.collect()

    val inputRdd = sc.hadoopMultibandGeoTiffRDD("hdfs://127.0.0.1:8020/user/admin/boutespaul/age_resize.tif")

    
    val coord = inputRdd
      .flatMap {
        case (proj, tile) => {
          val rasterExtent = RasterExtent(proj.extent, tile.cols, tile.rows)
          val rows = new ArrayBuffer[Info](tile.cols * tile.rows)
          tile.band(0).foreachDouble((col, row, z) => {
            val (lng, lat) = rasterExtent.gridToMap(col, row)
            val point = Point(lng, lat)
            val pointExtent = point.buffer(5.0)

            dsArray.foreach(l => {
              val pClass = Point(l.X_OK.toDouble, l.Y_OK.toDouble)
              if (pointExtent.contains(pClass)) {
                rows.append(Info(lng.toDouble, lat.toDouble, z, l))
              }
            })
          })
          rows
        }
      }.collect()

    println(coord.length)

    println("********")



  }

  process(sc)


}
