package com.example.cryptoapp.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.example.cryptoapp.data.database.AppDatabase
import com.example.cryptoapp.data.mapper.CoinMapper
import com.example.cryptoapp.data.network.ApiFactory
import kotlinx.coroutines.delay

class RefreshDataWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    private val coinInfoDao = AppDatabase.getInstance(context).coinPriceInfoDao()

    private val mapper = CoinMapper()

    private val apiService = ApiFactory.apiService

    override suspend fun doWork(): Result {
        while (true) {
            //трай кеч для отсутствия интернета, чтобы не было ошибки.
            try {//получаем топ популярных валют
                val topCoins = apiService.getTopCoinsInfo(limit = 10)
                //преобразовываем их в одну строку
                val fSyms = mapper.mapNamesListToString(topCoins)
                //по этой строке загружаем все необходимые данные из сети
                val jsonContainer = apiService.getFullPriceList(fSyms = fSyms)
                //потом эти данные из вида jsonContainer преобразуем в обхект dto
                val coinInfoDtoList = mapper.mapJsonContainerToListCoinInfo(jsonContainer)
                // а потом переводим их в объект баз данных
                val dbModelList = coinInfoDtoList.map { mapper.mapDtoToDbModel(it) }
                // вставляем в базу
                coinInfoDao.insertPriceList(dbModelList)
            } catch (e: Exception) {
            }
            delay(10000)
        }
    }

    companion object {
        const val NAME = "RefreshDataManager"

        fun makeRequest():OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>().build()
        }
    }
}