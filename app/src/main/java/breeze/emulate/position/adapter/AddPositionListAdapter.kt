package breeze.emulate.position.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import breeze.emulate.position.R
import breeze.emulate.position.bean.CoordinateBean
import java.util.*

class AddPositionListAdapter(context: Context, list: List<CoordinateBean>) : RecyclerView.Adapter<AddPositionListAdapter.MViewHolder>() {

    private val context = context
    private var list = list

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.item_position_location)
        val orderText: TextView = itemView.findViewById(R.id.item_position_order)
        val deleteView:ImageView = itemView.findViewById(R.id.item_position_delete)
        val alterView:ImageView = itemView.findViewById(R.id.item_position_alter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        return MViewHolder(LayoutInflater.from(context).inflate(R.layout.view_item_position, parent, false))
    }

    interface OnItemFunctionListener{
        fun onDelete(position: Int)
        fun onAlter(position: Int)
    }

    lateinit var listener:OnItemFunctionListener

    fun setOnItemFunctionListener(listener:OnItemFunctionListener) {
        this.listener = listener
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val coordinateBean = list[position]
        holder.orderText.text = (position+1).toString()
        holder.textView.text = "经度:${coordinateBean.longitude}\n纬度:${coordinateBean.latitude}"
        holder.deleteView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                listener.onDelete(position)
            }
        })
        holder.alterView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                listener.onAlter(position)
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(list: List<CoordinateBean>) {
        this.list = list
        notifyDataSetChanged()
    }
}