package io.github.redbrain.observant.servlets

import io.github.redbrain.observant.app.ObservantStack
import io.github.redbrain.observant.caches.ProcessCache
import io.github.redbrain.observant.configuration.Configuration
import io.github.redbrain.observant.models.{Liveness, ProcessDataModel}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport

case class ProcessDataPayload(key: String, payload: List[ProcessDataModel])

class ProcessResourcesServlet extends ObservantStack with JacksonJsonSupport with DataFactory {

  protected implicit val jsonFormats: Formats = DefaultFormats

  // Turn all respsonses into json
  before() {
    contentType = formats("json")
  }

  get("/keys") {
    ProcessCache.getHostKeys()
  }

  get("/liveness/:key") {
    val key = params("key")
    val data = ProcessCache.getCacheDataForKey(key)
    Liveness(
      isDataAliveForTimeout(
        data(data.length - 1).ts,
        Configuration.getHostsDataTimeout()
      )
    )
  }

  get("/rest/:key") {
    val key = params("key")
    ProcessDataPayload(key, ProcessCache.getCacheDataForKey(key))
  }

}
