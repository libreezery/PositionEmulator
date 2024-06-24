package breeze.emulate.position.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import breeze.emulate.position.R
import breeze.emulate.position.bean.AppLocationBean

class AppLocationRecyclerAdapter(context: Context, list:List<AppLocationBean>): RecyclerView.Adapter<AppLocationRecyclerAdapter.MViewHolder>() {

    val context = context
    val list = list

    class MViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val textTitle:TextView = itemView.findViewById(R.id.item_location_title)
        val textDescription:TextView = itemView.findViewById(R.id.item_location_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        return MViewHolder(LayoutInflater.from(context).inflate(R.layout.view_item_location,parent,false))
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val get = list.get(position)
        holder.textTitle.text = get.title
        holder.textDescription.text = get.description
    }

    override fun getItemCount(): Int {
        return list.size
    }
}