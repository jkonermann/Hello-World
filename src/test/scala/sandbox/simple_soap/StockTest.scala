package sandbox.simple_soap

import org.scalatest.FunSuite

class StockTest extends FunSuite {

  private val stock = new Stock

  def testValueBelongsToName(value:Option[Double], name:String): Unit ={
    val request = stock.createRequestXml(name)
    val response = stock.handleRequest(request)
    assert(value === stock.getStockPrice(response))
  }

  test("stockValue of apple is 1.0") {
    testValueBelongsToName(Some(1.0), "apple")
  }

  test("stockValue of google is 2.0") {
    testValueBelongsToName(Some(2.0), "google")
  }

  test("stockValue of IBM is 34.5") {
    testValueBelongsToName(Some(34.5), "IBM")
  }

  test("stockValue of microsoft is 3.0") {
    testValueBelongsToName(Some(3.0), "microsoft")
  }

  test("stockValue of abn is None") {
    testValueBelongsToName(None, "abn")
  }

}
