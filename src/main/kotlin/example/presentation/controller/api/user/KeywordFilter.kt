package example.presentation.controller.api.user

import jakarta.validation.constraints.*

data class KeywordFilter(@field:Size(max = 30)
                         val keyword: String)