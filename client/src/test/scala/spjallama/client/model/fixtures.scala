package spjallama.client.model

object ChatRequestJson:
  val simpleStreamingRequest: String =
    """
      |{
      |  "model" : "llama3.2",
      |  "messages" : [
      |    {
      |      "role" : "user",
      |      "content" : "why is the sky blue?"
      |    }
      |  ]
      |}
      |""".stripMargin.trim

  val simpleNoStreamingRequest: String =
    """
      |{
      |  "model" : "llama3.2",
      |  "messages" : [
      |    {
      |      "role" : "user",
      |      "content" : "why is the sky blue?"
      |    }
      |  ],
      |  "stream" : false
      |}
      |""".stripMargin.trim

object ChatResponseJson:
  val simpleStreamingResponse: String =
    """
      |{
      |  "model": "llama3.2",
      |  "created_at": "2023-08-04T08:52:19.385406455-07:00",
      |  "message": {
      |    "role": "assistant",
      |    "content": "The",
      |    "images": null
      |  },
      |  "done": false
      |}
      |""".stripMargin.trim

  val simpleFinalStreamingResponse: String =
    """
      |{
      |  "model": "llama3.2",
      |  "created_at": "2023-08-04T19:22:45.499127Z",
      |  "done": true,
      |  "total_duration": 4883583458,
      |  "load_duration": 1334875,
      |  "prompt_eval_count": 26,
      |  "prompt_eval_duration": 342546000,
      |  "eval_count": 282,
      |  "eval_duration": 4535599000
      |}
      |""".stripMargin.trim

  val simpleNonStreamingResponse: String =
    """
      |{
      |  "model": "llama3.2",
      |  "created_at": "2023-12-12T14:13:43.416799Z",
      |  "message": {
      |    "role": "assistant",
      |    "content": "Hello! How are you today?"
      |  },
      |  "done": true,
      |  "total_duration": 5191566416,
      |  "load_duration": 2154458,
      |  "prompt_eval_count": 26,
      |  "prompt_eval_duration": 383809000,
      |  "eval_count": 298,
      |  "eval_duration": 4799921000
      |}
      |""".stripMargin.trim
