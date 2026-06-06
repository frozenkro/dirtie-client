package com.frozenkro.dirtie_client.ui.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.frozenkro.dirtie_client.databinding.FragmentDeviceChartBinding
import com.frozenkro.dirtie_client.domain.models.Reading
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DeviceChartFragment : Fragment() {

    private var _binding: FragmentDeviceChartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeviceChartViewModel by viewModel()
    private val args: DeviceChartFragmentArgs by navArgs()
    private var currentTab = 0 // 0 = Capacitance, 1 = Temperature

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
        setupChart()
        observeState()

        val deviceId = args.deviceId
        if (deviceId != -1) {
            viewModel.loadDeviceData(deviceId)
        }
    }

    private fun setupTabs() {
        binding.chartTabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                refreshChartData()
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(false)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
            }
            axisLeft.setDrawGridLines(true)
            axisRight.isEnabled = false
            legend.isEnabled = true
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is DeviceChartViewModel.ChartState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.lineChart.isVisible = false
                            binding.errorText.isVisible = false
                        }
                        is DeviceChartViewModel.ChartState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.lineChart.isVisible = true
                            binding.errorText.isVisible = false
                            refreshChartData()
                        }
                        is DeviceChartViewModel.ChartState.Error -> {
                            binding.progressBar.isVisible = false
                            binding.lineChart.isVisible = false
                            binding.errorText.isVisible = true
                            binding.errorText.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun refreshChartData() {
        val state = viewModel.state.value
        if (state !is DeviceChartViewModel.ChartState.Success) return

        val readings = if (currentTab == 0) {
            state.capacitanceReadings
        } else {
            state.temperatureReadings
        }

        updateChart(readings)
    }

    private fun updateChart(readings: List<Reading>) {
        if (readings.isEmpty()) {
            binding.lineChart.clear()
            binding.lineChart.invalidate()
            return
        }

        val sorted = readings.sortedBy { it.timestamp }
        val timestamps = sorted.map { it.timestamp }

        val entries = sorted.mapIndexed { index, reading ->
            Entry(index.toFloat(), reading.value.toFloat())
        }

        val label = if (currentTab == 0) "Capacitance" else "Temperature"
        val color = if (currentTab == 0) android.graphics.Color.BLUE else android.graphics.Color.RED

        val dataSet = LineDataSet(entries, label).apply {
            this.color = color
            setCircleColor(color)
            lineWidth = 2f
            circleRadius = 3f
            setDrawValues(false)
        }

        binding.lineChart.xAxis.valueFormatter = DateAxisFormatter(timestamps)
        binding.lineChart.data = LineData(dataSet)
        binding.lineChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class DateAxisFormatter(
        private val timestamps: List<Long>
    ) : ValueFormatter() {
        private val formatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault())

        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index in timestamps.indices) {
                val instant = Instant.ofEpochSecond(timestamps[index])
                formatter.format(instant)
            } else {
                ""
            }
        }
    }
}
