package com.mohamedhamza.weatherly.data

import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.model.FavouriteLocation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest{


    val alert1 = Alert(1, "Cairo", "القاهرة", "EG", "Africa/Cairo", 123, 567)
    val alert2 = Alert(2, "Alex", "الإسكندرية", "EG", "Africa/Cairo", 123, 567)
    val alert3 = Alert(3, "Giza", "الجيزة", "EG", "Africa/Cairo", 123, 567)
    val alert4 = Alert(4, "Aswan", "أسوان", "EG", "Africa/Cairo", 123, 567)

    val alerts = listOf(alert1, alert2, alert3, alert4)

    private lateinit var localSource: FakeDataSource
    private lateinit var repository: WeatherRepository


    @Before
    fun setUp(){
        localSource = FakeDataSource(alerts.toMutableList())
        repository = WeatherRepository.getInstance(localSource)
    }


    @Test
    fun getFavouriteLocations_returnsEmptyList_whenNoLocationsInserted() = runBlockingTest {
        val favouriteLocations = repository.getFavouriteLocations()
        val firstEmit = favouriteLocations.first()
        assertThat(firstEmit.size, `is`(0))
    }

    @Test
    fun getFavouriteLocations_returnsListWithLocations() = runBlockingTest {
        val location1 = FavouriteLocation(1, "Cairo", "القاهرة", "EG", 30.0, 30.0, "Africa/Cairo", 0,false,false,  )
        val location2 = FavouriteLocation(2, "Alex", "الإسكندرية", "EG", 30.0, 30.0, "Africa/Cairo", 0,false,false,  )
        repository.insertLocation(location1)
        repository.insertLocation(location2)
        val favouriteLocations = repository.getFavouriteLocations()
        val firstEmit = favouriteLocations.first()
        assertThat(firstEmit.size, `is`(2))
    }

    @Test
    fun getFavouriteLocations_returnsListWithOne() = runBlockingTest {
        val location1 = FavouriteLocation(1, "Cairo", "القاهرة", "EG", 30.0, 30.0, "Africa/Cairo", 0,false,false,  )
        val location2 = FavouriteLocation(2, "Alex", "الإسكندرية", "EG", 30.0, 30.0, "Africa/Cairo", 0,false,false,  )
        repository.insertLocation(location1)
        repository.insertLocation(location2)
        repository.deleteLocation(location1)
        val favouriteLocations = repository.getFavouriteLocations()
        val firstEmit = favouriteLocations.first()
        assertThat(firstEmit.size, `is`(1))
    }

    @Test
    fun getAlerts_returnsFour_atFirst() = runBlockingTest {
        val alerts = repository.getAlerts()
        assertThat(alerts.value?.size, `is`(4))
    }


    @Test
    fun getAlerts_returnsThree_afterDeleteOne() = runBlockingTest {
        repository.deleteAlert(alert1)
        val alerts = repository.getAlerts()
        assertThat(alerts.value?.size, `is`(3))
    }


    @After
    fun tearDown() = runBlockingTest{
        repository.deleteAllAlerts()
        repository.deleteAllLocations()
    }







}