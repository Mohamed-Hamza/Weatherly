package com.mohamedhamza.weatherly.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mohamedhamza.weatherly.data.IWeatherRepository
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.viewmodel.LiveDataExt.getOrAwaitValue
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AlertsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: AlertsViewModel
    lateinit var repository: IWeatherRepository

    @Before
    fun setupViewModel() {
        repository = FakeWeatherRepository()
        viewModel = AlertsViewModel(repository)
    }


    @Test
    fun getAlerts_empty_atFirst() {
        val alerts = viewModel.alerts.getOrAwaitValue{}
        assert(alerts.isEmpty())
    }

    @Test
    fun getAlerts_notEmpty_afterInsertingAlert() = runBlockingTest {
        val alert = Alert(1, "Cairo", "القاهرة", "EG", "Africa/Cairo", 123, 567)
        viewModel.insertAlert(alert)
        val alerts = viewModel.alerts.getOrAwaitValue{}
        Assert.assertThat(alerts.size, `is`(1))
    }


    @Test
    fun getAlerts_notOne_afterInsertAndDelete() = runBlockingTest {
        val alert1 = Alert(1, "Cairo", "القاهرة", "EG", "Africa/Cairo", 123, 567)
        val alert2 = Alert(2, "Alex", "الإسكندرية", "EG", "Africa/Cairo", 123, 567)
        viewModel.insertAlert(alert1)
        viewModel.insertAlert(alert2)
        viewModel.deleteAlert(alert1)
        val alerts = viewModel.alerts.getOrAwaitValue{}
        Assert.assertThat(alerts.size, `is`(1))
    }


    @After
    fun tearDown() = runBlockingTest {
        repository.deleteAllAlerts()
    }





}