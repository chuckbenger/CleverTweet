##CleverTweet
Simple Scala library that takes a twitter users newest tweet sends it to CleverBot
and replies back to the user with a `clever` response.

#Instructions
Simply pass a list or single instance of TwitterBotConfig for each account to reply to

    val users = List(TwitterBotConfig(TwitterInstance, "username", sleep time, reply percent),
                                      TwitterBotConfig(TwitterInstance, "username", sleep time, reply percent))