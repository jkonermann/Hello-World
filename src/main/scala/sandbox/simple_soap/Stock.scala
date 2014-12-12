package sandbox.simple_soap

import spray.http.{HttpResponse, HttpRequest}

import scala.xml.{XML, NodeSeq}

class Stock() {

  private val stockValues = Map(
    "apple" -> 1.0,
    "google" -> 2.0,
    "IBM" -> 34.5,
    "microsoft" -> 3.0
  )

  def createRequestXml(name: String) = {
    <soap:Envelope
    xmlns:soap="http://www.w3.org/2001/12/soap-envelope"
    soap:encodingStyle="http://www.w3.org/2001/12/soap-encoding">
      <soap:Body xmlns:m="http://www.example.org/stock">
        <m:GetStockPrice>
          <m:StockName>{name}</m:StockName>
        </m:GetStockPrice>
      </soap:Body>
    </soap:Envelope>
  }

  private def createResponseXml(stockName: String, stockPrice: Option[Double]) = {
    if (stockPrice.isDefined) {
      <soap:Envelope
      xmlns:soap="http://www.w3.org/2001/12/soap-envelope"
      soap:encodingStyle="http://www.w3.org/2001/12/soap-encoding">
        <soap:Body xmlns:m="http://www.example.org/stock">
          <m:GetStockPriceResponse>
            <m:Price>{stockPrice.get}</m:Price>
          </m:GetStockPriceResponse>
        </soap:Body>
      </soap:Envelope>
    } else {
      <soap:Envelope
      xmlns:soap="http://www.w3.org/2001/12/soap-envelope"
      xmlns:xsi="http://www.w3.org/1999/XMLSchema-instance"
      xmlns:xsd="http://www.w3.org/1999/XMLSchema">
        soap:encodingStyle="http://www.w3.org/2001/12/soap-encoding">
        <soap:Body xmlns:m="http://www.example.org/stock">
          <soap:Fault>
            <faultcode xsi:type="xsd:string">soap:Client</faultcode>
            <faultstring xsi:type="xsd:string">
              Failed to getStockPrice for ({stockName}).
            </faultstring>
          </soap:Fault>
        </soap:Body>
      </soap:Envelope>
    }
  }

  private def getStockPrice(stockName: String) = {
    stockValues.get(stockName)
  }

  def handleRequest(request: NodeSeq) = {
    val stockName = (request \\ "StockName").text
    val stockPrice = getStockPrice(stockName)
    createResponseXml(stockName, stockPrice)
  }

  def createHttpResponse(request:HttpRequest) = {
    val reqXml = XML.loadString(request.message.entity.data.asString.trim())
    val respXml = handleRequest(reqXml)
    HttpResponse(entity = respXml.toString())
  }

  def getStockPrice(response: NodeSeq) = {
    val price = (response \\ "Price").text
    if (price.isEmpty) {
      None
    } else {
      Some(price.toDouble)
    }
  }

  def getStockPrice(response:HttpResponse):Option[Double] = {
    val resXml = XML.loadString(response.message.entity.data.asString.trim())
    getStockPrice(resXml)
  }

}
