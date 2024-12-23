package com.frozenkro.dirtie_client.ui.devices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.frozenkro.dirtie_client.databinding.ItemDeviceBinding
import com.frozenkro.dirtie_client.domain.models.Device

class DeviceAdapter : ListAdapter<Device, DeviceAdapter.DeviceViewHolder>(DeviceDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeviceViewHolder(binding)
    }
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DeviceViewHolder(
        private val binding: ItemDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(device: Device){
            binding.deviceName.text = device.name
            binding.deviceReading.text = device.currentCapacitance.toString()
        }
    }
}

class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }
}
