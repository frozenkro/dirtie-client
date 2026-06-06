package com.frozenkro.dirtie_client.ui.devices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.frozenkro.dirtie_client.databinding.ItemDeviceBinding
import com.frozenkro.dirtie_client.domain.models.Device

class DeviceAdapter(
    private val onDeviceClick: (Int) -> Unit
) : ListAdapter<Device, DeviceAdapter.DeviceViewHolder>(DeviceDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeviceViewHolder(binding)
    }
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position), onDeviceClick)
    }

    class DeviceViewHolder(
        private val binding: ItemDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(device: Device, onClick: (Int) -> Unit){
            binding.deviceName.text = device.name
            binding.deviceReading.text = "Cap: ${device.currentCapacitance.toInt()}"
            binding.deviceTemp.text = "Temp: ${device.currentTemperature.toInt()}°C"
            binding.root.setOnClickListener { onClick(device.id) }
        }
    }
}

class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }
}
