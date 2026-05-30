package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.di

import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.QuoteApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.QuoteRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppContainer {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://zenquotes.io/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val quoteApi: QuoteApi = retrofit.create(QuoteApi::class.java)

    val quoteRepository: QuoteRepository = QuoteRepository(quoteApi)
}