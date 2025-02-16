package filodb.core.query

import scala.concurrent.duration.FiniteDuration

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

object QueryConfig {
  val DefaultVectorsLimit = 150

  def apply(queryConfig: Config): QueryConfig = {
    val askTimeout = queryConfig.as[FiniteDuration]("ask-timeout")
    val staleSampleAfterMs = queryConfig.getDuration("stale-sample-after").toMillis
    val minStepMs = queryConfig.getDuration("min-step").toMillis
    val fastReduceMaxWindows = queryConfig.getInt("fastreduce-max-windows")
    val routingConfig = queryConfig.getConfig("routing")
    val parser = queryConfig.as[String]("parser")
    val translatePromToFilodbHistogram = queryConfig.getBoolean("translate-prom-to-filodb-histogram")
    val fasterRateEnabled = queryConfig.as[Option[Boolean]]("faster-rate").getOrElse(false)
    val enforceResultByteLimit = queryConfig.as[Boolean]("enforce-result-byte-limit")
    val allowPartialResultsMetadataQuery = queryConfig.getBoolean("allow-partial-results-metadataquery")
    val allowPartialResultsRangeQuery = queryConfig.getBoolean("allow-partial-results-rangequery")
    val grpcDenyList = queryConfig.getString("grpc.partitions-deny-list")
    QueryConfig(askTimeout, staleSampleAfterMs, minStepMs, fastReduceMaxWindows, parser, translatePromToFilodbHistogram,
      fasterRateEnabled, routingConfig.as[Option[Long]]("remote.http.timeout"),
      routingConfig.as[Option[String]]("remote.http.endpoint"), enforceResultByteLimit,
      allowPartialResultsRangeQuery, allowPartialResultsMetadataQuery,
      grpcDenyList.split(",").map(_.trim.toLowerCase).toSet)
  }

  import scala.concurrent.duration._
  /**
   * IMPORTANT: Use this for testing only, using this for anything other than testing may yield undesired behavior
   */
  val unitTestingQueryConfig = QueryConfig(askTimeout = 10.seconds,
                                           staleSampleAfterMs = 5.minutes.toMillis,
                                           minStepMs = 1,
                                           fastReduceMaxWindows = 50,
                                           parser = "antlr",
                                           translatePromToFilodbHistogram = true,
                                           fasterRateEnabled = true,
                                           remoteHttpTimeoutMs = None,
                                           remoteHttpEndpoint = None,
                                           enforceResultByteLimit = false,
                                           allowPartialResultsRangeQuery = false,
                                           allowPartialResultsMetadataQuery = true)
}

case class QueryConfig(askTimeout: FiniteDuration,
                       staleSampleAfterMs: Long,
                       minStepMs: Long,
                       fastReduceMaxWindows: Int,
                       parser: String,
                       translatePromToFilodbHistogram: Boolean,
                       fasterRateEnabled: Boolean,
                       remoteHttpTimeoutMs: Option[Long],
                       remoteHttpEndpoint: Option[String],
                       enforceResultByteLimit: Boolean = false,
                       allowPartialResultsRangeQuery: Boolean = false,
                       allowPartialResultsMetadataQuery: Boolean = true,
                       grpcPartitionsDenyList: Set[String] = Set.empty)
