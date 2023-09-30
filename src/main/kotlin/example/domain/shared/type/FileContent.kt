package example.domain.shared.type

import org.springframework.http.*
import java.io.*

class FileContent(val type: MediaType,
                  val size: Int,
                  val content: InputStream)