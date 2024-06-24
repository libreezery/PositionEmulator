package breeze.emulate.position.activity

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import breeze.emulate.position.R
import breeze.emulate.position.adapter.AddPositionListAdapter
import breeze.emulate.position.adapter.AddPositionViewAdapter
import breeze.emulate.position.bean.AppLocationBean
import breeze.emulate.position.bean.CoordinateBean
import breeze.emulate.position.bean.dao.AppLocationDao
import breeze.emulate.position.bean.impl.AppLocationDaoImpl
import breeze.emulate.position.bean.dao.CoordinateDao
import breeze.emulate.position.bean.impl.CoordinateDaoImpl
import breeze.lib.carnation.activity.OrchidBaseActivity
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText

class AddPositionActivity : OrchidBaseActivity() {

    private val TAG = "AddPositionActivity"
    private lateinit var gaoMap: MapView
    private var isAddMarker = true
    private var listLatBean: MutableList<CoordinateBean> = mutableListOf()
    private var listMarkers: MutableList<Marker> = mutableListOf()
    private lateinit var viewPager: ViewPager
    private lateinit var views: List<View>
    private lateinit var viewPagerAdapter: AddPositionViewAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var linearLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var addPositionListAdapter: AddPositionListAdapter
    private lateinit var inputEditSaveName: TextInputEditText

    // 设置速度功能

    private lateinit var inputOfLatitude: TextInputEditText
    private lateinit var inputOfLongitude: TextInputEditText
    private lateinit var buttonOfSavePosition: Button
    private val windowOfAlterPositionView: View
        get() {
            val inflate = layoutInflater.inflate(R.layout.window_modify_coordinate, null)
            inputOfLatitude = inflate.findViewById(R.id.window_modify_position_inputOfLatitude)
            inputOfLongitude = inflate.findViewById(R.id.window_modify_position_inputOfLongitude)
            buttonOfSavePosition = inflate.findViewById(R.id.window_modify_position_bottomOfSave)
            inflate.findViewById<TextView>(R.id.window_modify_position_buttonOfQuit).setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    inputOfLatitude.setText("")
                    inputOfLongitude.setText("")
                    if (windowOfAlterPosition.isShowing) {
                        windowOfAlterPosition.dismiss()
                    }
                    changeWindowBackground(false)
                }
            })
            return inflate
        }
    private lateinit var windowOfAlterPosition: PopupWindow
    private val appLocationDao:AppLocationDao = AppLocationDaoImpl(this)
    private val coordinateDao:CoordinateDao = CoordinateDaoImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_position)

        val materialToolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(materialToolbar)

        initializeView()
        initializeMap(savedInstanceState)
        initializeLocationList()
        initializeAllClickListener()
    }

    private fun initializeAllClickListener() {
        // clear all markers and positions
        views[0].findViewById<TextView>(R.id.add_position_clearAll).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                listLatBean.clear()
                for (marker in listMarkers) {
                    marker.remove()
                }
                listMarkers.clear()
                addPositionListAdapter.update(listLatBean)
                initializeLocationList()
                toast("清除成功")
            }
        })
        // 设置速度功能

    }

    private fun initializeLocationList() {
        if (listLatBean.isEmpty()) {
            linearLayout.visibility = View.VISIBLE
        } else {
            linearLayout.visibility = View.GONE
        }
    }

    private fun initializeMap(savedInstanceState: Bundle?) {
        gaoMap.onCreate(savedInstanceState)
        // initial map style bitch!!!!
        // initial the object of map
        val myLocationStyle = MyLocationStyle().apply {
            interval(3000) // set the interval of get position
            showMyLocation(true) // show where you are
            myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) // set the type
        }
        // set map style
        gaoMap.map.apply {
            isMyLocationEnabled = true // enable my location
            setMyLocationStyle(myLocationStyle) // set style
            uiSettings.isMyLocationButtonEnabled = true
            // create location point when user click the map
            setOnMapClickListener(object : AMap.OnMapClickListener {
                override fun onMapClick(latLng: LatLng?) {
                    addPositionPoint(latLng)
                    // 绘制坐标线
                    /*val apply = PolylineOptions().apply {
                        width(5f)
                        color(resources.getColor(R.color.colorPrimary))
                    }
                    for (i in listLatBean) {
                        val latLng1 = LatLng(i.latitude, i.longitude)
                        apply.add(latLng1)
                    }
                    addPolyline(apply)*/
                }
            })
        }
    }

    private fun addMarkerToMap(latLng: LatLng) {
        addMarkerToMap(-1, latLng)
    }

    private fun addMarkerToMap(index: Int, latLng: LatLng) {
        if (isAddMarker) {
            val addMarker = gaoMap.map.addMarker(MarkerOptions().apply {
                position(latLng)
                title("标记点")
                snippet("经度:${latLng.longitude}\n纬度:${latLng.latitude}")
            })
            if (index > -1) {
                listMarkers.add(index, addMarker)
            } else {
                listMarkers.add(addMarker)
            }
        }
    }

    private fun initializeView() {
        val view_main = layoutInflater.inflate(R.layout.view_add_position_main, null)
        val view_list = layoutInflater.inflate(R.layout.view_add_position_list, null)
        views = listOf(view_main, view_list)
        viewPager = findViewById(R.id.add_position_viewPager)
        tabLayout = findViewById(R.id.add_position_tabLayout)
        // initial tab layout
        tabLayout.apply {
            addTab(tabLayout.newTab().apply {
                text = "主页"
            })
            addTab(tabLayout.newTab().apply {
                text = "坐标集"
            })
        }
        // start to initial widget in separated view
        gaoMap = views[0].findViewById(R.id.add_position_map)
        inputEditSaveName = views[0].findViewById(R.id.add_position_saveName)
        linearLayout = views[1].findViewById(R.id.add_position_linearLayout)
        recyclerView = views[1].findViewById(R.id.add_position_recyclerView)
        viewPagerAdapter = AddPositionViewAdapter(this, views)
        viewPager.adapter = viewPagerAdapter
        // add listener to viewPager and tags
//        tabLayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        // initial the coordinate list
        addPositionListAdapter = AddPositionListAdapter(this, listLatBean)
        recyclerView.apply {
            adapter = addPositionListAdapter
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            layoutManager = LinearLayoutManager(context)
        }
        // initial list listener
        addPositionListAdapter.setOnItemFunctionListener(object : AddPositionListAdapter.OnItemFunctionListener {
            override fun onDelete(position: Int) {
                removePosition(position)
            }

            override fun onAlter(position: Int) {
                if (!windowOfAlterPosition.isShowing) {
                    windowOfAlterPosition.showAsDropDown(window.decorView, Gravity.BOTTOM, 0, 0)
                    changeWindowBackground(true)
                }
                val coordinateBean = listLatBean[position]
                val marker = listMarkers[position]
                inputOfLongitude.setText(coordinateBean.longitude.toString())
                inputOfLatitude.setText(coordinateBean.latitude.toString())
                buttonOfSavePosition.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        val longitude1 = inputOfLongitude.text.toString().toDouble()
                        val latitude1 = inputOfLatitude.text.toString().toDouble()
                        val latLng = LatLng(latitude1, longitude1)
                        coordinateBean.latitude = latitude1
                        coordinateBean.longitude = longitude1
                        marker.remove()
                        listMarkers.removeAt(position)
                        addMarkerToMap(position, latLng)
                        addPositionListAdapter.update(listLatBean)
                        toast("修改成功")
                        closeWindowOfAddPosition()
                    }
                })
            }
        })
        // 设置速度功能2

        windowOfAlterPosition = PopupWindow(windowOfAlterPositionView, -1, -1).apply {
            animationStyle = R.style.PopupWindowAnimation
            isFocusable = true
            setOnDismissListener(object : PopupWindow.OnDismissListener {
                override fun onDismiss() {
                    changeWindowBackground(false)
                }
            })
        }
    }

    private fun removePosition(position: Int) {
        listLatBean.removeAt(position)
        if (isAddMarker) {
            listMarkers[position].remove()
            listMarkers.removeAt(position)
        }
        initializeLocationList()
        addPositionListAdapter.update(listLatBean)
    }

    /**
     * add position and mark to map and list
     */
    private fun addPositionPoint(latLng: LatLng?) {
        val latitude = latLng?.latitude // 纬度
        val longitude = latLng?.longitude // 经度
        val coordinateBean = CoordinateBean()
        coordinateBean.latitude = latitude!!
        coordinateBean.longitude = longitude!!
        addCoordinateToList(coordinateBean)
        addMarkerToMap(latLng)

    }

    private fun addCoordinateToList(p0: CoordinateBean) {
        listLatBean.add(p0)
        initializeLocationList()
        addPositionListAdapter.update(listLatBean)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_position_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add_position_send) {
            val saveName = inputEditSaveName.text.toString()
            if (saveName.isNotBlank()) {
                var saveSuccess = true;
                if (listLatBean.isEmpty()) {
                    toast("请添加坐标")
                    return false
                }
                // 创建坐标
                val appLocationBean = AppLocationBean().apply {
                    title = saveName
                    createTime = System.currentTimeMillis()
                    description = ""
                }
                appLocationDao.add(appLocationBean).also {
                    saveSuccess = it;
                }
                for (bean in listLatBean) {
                    bean.appLocationBean = appLocationBean
                    saveSuccess = coordinateDao.add(bean)
                }
                if (saveSuccess) {
                    toast("保存成功")
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    toast("保存失败")
                }
            } else {
                inputEditSaveName.error = "请输入名称"
            }
        } else if (item.itemId == R.id.menu_add_position_add) {
            inputOfLatitude.setText("")
            inputOfLongitude.setText("")
            changeWindowBackground(true)
            if (windowOfAlterPosition.isShowing) {
                windowOfAlterPosition.dismiss()
            } else {
                windowOfAlterPosition.showAsDropDown(window.decorView, Gravity.BOTTOM, 0, 0)
            }
            buttonOfSavePosition.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val longitude1 = inputOfLongitude.text.toString().toDouble()
                    val latitude1 = inputOfLatitude.text.toString().toDouble()
                    addPositionPoint(LatLng(latitude1, longitude1))
                    toast("添加成功")
                    closeWindowOfAddPosition()
                }
            })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun closeWindowOfAddPosition() {
        if (windowOfAlterPosition.isShowing) {
            windowOfAlterPosition.dismiss()
        }
        changeWindowBackground(false)
    }

    private fun changeWindowBackground(isOpen: Boolean) {
        val attributes = window.attributes
        attributes.alpha = if (isOpen) {
            0.7f
        } else {
            1.0f
        }
        window.attributes = attributes
    }
}