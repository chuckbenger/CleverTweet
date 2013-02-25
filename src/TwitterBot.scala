import java.io.{BufferedReader, FileReader, File, PrintWriter}
import twitter4j.{Twitter, Status, Paging}
import util.Random

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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

case class TwitterBotConfig(twitter: Twitter, user: String, sleep: Int, replyChance: Double)

object TwitterBot {

  /**
   * Creates a new twitter bot on a seperate thread
   * @param config Twitter bot configuration for each user
   */
  def apply(config: TwitterBotConfig): Unit =
    new Thread(new TwitterBot(config)).start

  /**
   * Creates a new twitter bot for each client
   * @param config Twitter bot configuration for each user
   */
  def apply(config: List[TwitterBotConfig]): Unit =
    config.foreach(apply)
}

class TwitterBot(config: TwitterBotConfig) extends Runnable {

  import config._

  val cleverBot = CleverBot()
  val paging = new Paging(1, 1)
  val userFile = new File(s"$user.txt")
  var lastTweetID: Long = 0

  def run() {

    println(this)
    readIDFromFile

    while (true) {
      val userTimeLine = twitter.getUserTimeline(user, paging)
      val iterator = userTimeLine.iterator

      if (iterator.hasNext)
        handleTweet(iterator.next)

      Thread.sleep(1000 * 60 * sleep)
    }
  }

  def handleTweet(tweet: Status): Unit = {

    if (tweet.getId != lastTweetID) {

      lastTweetID = tweet.getId

      if (shouldReply(replyChance)) {
        val replyMessage = cleverBot.think(tweet.getText)
        if (replyMessage != "")
          reply(tweet.getText, replyMessage)
      } else
        println(s"$user skipping tweet")

      writeIDToFile(lastTweetID)
    }
  }

  def reply(tweet: String, message: String): Unit = try {
    twitter.updateStatus(s"@$user $message")
    println(s"$user\n\ttweet = $tweet \n\treply = $message ")
  } catch {
    case ex: Throwable => println(ex)
  }

  def shouldReply(successPercent: Double): Boolean = Random.nextDouble() <= (successPercent % 1.0)

  def readIDFromFile: Unit = try {
    val reader = new BufferedReader(new FileReader(userFile))
    lastTweetID = reader.readLine.toLong
    println(s"$user last tweet id was $lastTweetID")
    reader.close
  } catch {
    case ex: Throwable => println(ex)
  }

  def writeIDToFile(id: Long): Unit = try {
    val writer = new PrintWriter(userFile)
    writer.write(id.toString)
    writer.close()
  } catch {
    case ex: Throwable => println(ex)
  }

  override def toString: String = {
    s"""

      Starting $user
      CONFIG
      ================================
      Sleep Time = $sleep minutes
      Reply Rate = $replyChance%
      ================================
    """
  }

}














