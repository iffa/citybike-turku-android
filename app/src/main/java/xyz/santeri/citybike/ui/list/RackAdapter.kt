package xyz.santeri.citybike.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.ajalt.timberkt.Timber
import com.lucasurbas.listitemview.ListItemView
import xyz.santeri.citybike.R
import xyz.santeri.citybike.data.model.Availability
import xyz.santeri.citybike.data.model.Rack

class RackAdapter : RecyclerView.Adapter<RackAdapter.Holder>() {
    private val items = mutableListOf<Rack>()

    fun newItems(items: List<Rack>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(
                R.layout.view_holder_rack,
                parent,
                false
        ))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val rack = items[position]

        holder.view.title = rack.properties.name
        holder.view.subtitle = holder.view.resources.getString(R.string.tv_list_racks_availability,
                rack.properties.bikesAvailable, rack.properties.slotsTotal)

        val color = when (rack.availability) {
            Availability.GOOD -> R.color.green_500
            Availability.WEAK -> R.color.orange_500
            Availability.EMPTY -> R.color.red_500
        }

        holder.view.iconColor = ContextCompat.getColor(holder.view.context, color)

        holder.view.setOnClickListener {
            Timber.d { "${rack.properties.name} clicked in list" }
        }
    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view: ListItemView = itemView.findViewById(R.id.item)
    }
}