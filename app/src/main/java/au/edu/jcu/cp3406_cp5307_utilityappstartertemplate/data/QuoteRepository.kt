package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data

class QuoteRepository(
    private val quoteApi: QuoteApi
) {
    suspend fun getFocusQuote(): String {
        return try {
            val response = quoteApi.getRandomQuote()
            val quote = response.firstOrNull()

            if (quote != null) {
                "\"${quote.q}\" — ${quote.a}"
            } else {
                "Stay focused. Small steps still move you forward."
            }
        } catch (exception: Exception) {
            "Stay focused. Small steps still move you forward."
        }
    }
}