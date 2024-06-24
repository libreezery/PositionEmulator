package breeze.emulate.position.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import breeze.emulate.position.R
import breeze.emulate.position.bean.AppLocationBean
import java.text.SimpleDateFormat

class AppHomeCordsAdapter(context: Context, list: List<AppLocationBean>) : RecyclerView.Adapter<AppHomeCordsAdapter.MViewHolder>() {

    private val context = context
    private val TAG = "AppHomeCordsAdapter"

    private var data: List<AppLocationBean> = list

    fun update(list: List<AppLocationBean>) {
        this.data = list
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, data: AppLocationBean)
    }

    lateinit var onItemClickListsner: OnItemClickListener

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val baseView: LinearLayout = itemView.findViewById(R.id.view_item_home_cords_baseView)
        val title: TextView = itemView.findViewById(R.id.view_item_home_cords_Title)
        val createTime: TextView = itemView.findViewById(R.id.view_item_home_cords_createTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        return MViewHolder(LayoutInflater.from(context).inflate(R.layout.view_item_home_cords, parent, false))
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        val appLocationBean = data[position]
        holder.title.text = appLocationBean.title
        holder.createTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm").format(appLocationBean.createTime)
        holder.baseView.setOnClickListener {
            onItemClickListsner.onItemClick(position, appLocationBean)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}