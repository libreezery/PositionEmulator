package breeze.emulate.position.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import breeze.emulate.position.R
import breeze.emulate.position.bean.AppLocationBean
import breeze.emulate.position.bean.CoordinateBean
import breeze.emulate.position.bean.dao.AppLocationDao
import breeze.emulate.position.bean.dao.CoordinateDao
import breeze.emulate.position.bean.impl.AppLocationDaoImpl
import breeze.emulate.position.bean.impl.CoordinateDaoImpl
import breeze.lib.carnation.activity.OrchidBaseActivity
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.MarkerOptions
import com.amap.api.maps2d.model.MyLocationStyle
import com.amap.api.maps2d.model.PolylineOptions
import com.google.android.material.textfield.TextInputEditText

class RunningActivity : OrchidBaseActivity() {

    private var isRUN: Boolean = false
    private var allLats: MutableList<CoordinateBean> = mutableListOf()
    private lateinit var locationBean: AppLocationBean
    private lateinit var map: MapView
    private lateinit var startRunBtn: Button

    private val dialogAboutSpeed: AlertDialog
        get() {
            return AlertDialog.Builder(this).apply {
                setTitle("关于")
                setMessage(getString(R.string.about_speed))
                setPositiveButton("确定", null)
            }.create()
        }
    private var changeSpeed = 10
    private lateinit var seekOfSpeed: SeekBar
    private lateinit var textOfSpeed: TextView
    private val appLocationDao:AppLocationDao = AppLocationDaoImpl(this)
    private val coordinateDao:CoordinateDao = CoordinateDaoImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)
        map = findViewById(R.id.add_position_map)
        map.onCreate(savedInstanceState)

        // 初始化控件
        setAppToolbar(R.id.toolbar)
        // 获取id
        val id = intent.getIntExtra("id", -1)
        this.locationBean = appLocationDao.findById(id)
        this.allLats = coordinateDao.findForAppLocation(locationBean)
        // initial speed widgets
        textOfSpeed = findViewById(R.id.add_position_textOfSpeed)
        seekOfSpeed = findViewById(R.id.add_position_seekOfSpeed)
        startRunBtn = findViewById(R.id.main_start_btn)

        // 设置参数
        findViewById<TextInputEditText>(R.id.add_position_saveName).setText(locationBean.title)

        map.map.apply {
            val latLng2 = LatLng(allLats[0].latitude, allLats[0].longitude)
            map.map.moveCamera(CameraUpdateFactory.changeLatLng(latLng2))
            isMyLocationEnabled = true
            setMyLocationStyle(MyLocationStyle().apply {
                interval(3000) // set the interval of get position
                showMyLocation(true) // show where you are
                myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) // set the type
            })
            uiSettings.isMyLocationButtonEnabled = true
            addPolyline(PolylineOptions().apply {
                width(5f)
                color(resources.getColor(R.color.colorPrimary))
            }.also {
                for (i in allLats) {
                    val latLng1 = LatLng(i.latitude, i.longitude)
                    addMarker(MarkerOptions().apply {
                        position(latLng1)
                        title("标记点")
                        snippet("经度:${latLng1.longitude}\n纬度:${latLng1.latitude}")
                    })
                    it.add(latLng1)
                }
                it
            })
        }

        // show the info about speed
        findViewById<ImageView>(R.id.add_position_aboutSpeed).setOnClickListener {
            if (dialogAboutSpeed.isShowing)
                dialogAboutSpeed.dismiss()
            dialogAboutSpeed.show()
        }
        // initial the widget listener
        seekOfSpeed.apply {
            max = 60
            progress = changeSpeed
        }
        seekOfSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var progress2 = progress
                if (progress2 <= 0) {
                    progress2 = 1
                }
                changeSpeed = progress2
                textOfSpeed.text = "速度调节:${progress2}秒/段"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        startRunBtn.setOnClickListener {
            isRUN = !isRUN
            val intent = Intent("breeze.emulate.position.hook.UPDATE_SETTINGS")
            intent.putExtra("isRun", isRUN)
            intent.putExtra("speedMillion", changeSpeed)
            intent.putExtra("id", this.locationBean.id)
            sendBroadcast(intent)
            startRunBtn.text = if (isRUN) "关闭模拟定位" else "开始模拟定位"
        }

    }

    companion object {
        fun startActivity(context: Context, id: Int) {
            context.startActivity(Intent(context, RunningActivity::class.java).apply {
                putExtra("id", id)
            })
        }
    }
}