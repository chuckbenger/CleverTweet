import io.{BufferedSource, Source}
import java.io.DataOutputStream
import java.net.{HttpURLConnection, URL}
import java.security.MessageDigest

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
object CleverBot {

  val webserviceLink = new URL("http://cleverbot.com/webservicemin")

  def apply(): CleverBot = new CleverBot
}

class CleverBot {

  val postData = collection.mutable.LinkedHashMap("start" -> "y",
    "icognoid" -> "wsf",
    "fno" -> "0",
    "sub" -> "Say",
    "islearning" -> "1",
    "cleanslate" -> "false")

  def think(thought: String): String = {
    postData += "stimulus" -> thought
    postData += "icognocheck" -> MD5(makeQueryString)
    send(makeQueryString)
  }

  private[this] def send(queryString: String): String =
    writeAndReceive(queryString).getOrElse(List("")).head


  private[this] def writeAndReceive(queryString: String): Option[List[String]] = {

    try {
      val connection = CleverBot.webserviceLink.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("POST")
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
      connection.setRequestProperty("Content-Length", Integer.toString(queryString.getBytes.length))
      connection.setRequestProperty("Content-Language", "en-US")
      connection.setUseCaches(false)
      connection.setDoInput(true)
      connection.setDoOutput(true)

      val wr = new DataOutputStream(connection.getOutputStream())
      wr.writeBytes(queryString)
      wr.flush()
      wr.close()

     val result = Some(Source.fromInputStream(connection.getInputStream).getLines.toList)
     connection.disconnect
     result
    }
    catch {
      case ex: Throwable => None
    }
  }

  private[this] def makeQueryString = postData.map(x => x._1 + "=" + x._2).mkString("&")

  private[this] def MD5(queryString: String): String =
    BigInt(MessageDigest.getInstance("MD5").digest(queryString.substring(9, 29).getBytes)).formatted("%1$032X")

}















