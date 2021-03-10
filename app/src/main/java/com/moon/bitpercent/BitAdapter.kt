package com.moon.bitpercent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moon.bitpercent.room.BitEntity
import java.util.*

class BitAdapter(val items: List<BitEntity>?) : RecyclerView.Adapter<BitAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_info, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items == null) {
            return
        }
        items[position].run {
            holder.type.text = type
            holder.price.text = price.toString()
            holder.percent.text = percent.toString()
            holder.result.text = result.toString()
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    fun setItems(list: List<BitEntity>) {
        (items as ArrayList).run {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var type: TextView = view.findViewById(R.id.type)
        var price: TextView = view.findViewById(R.id.price)
        var percent: TextView = view.findViewById(R.id.percent)
        var result: TextView = view.findViewById(R.id.result)
    }

}