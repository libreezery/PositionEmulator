package breeze.emulate.position.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import breeze.emulate.position.R
import breeze.emulate.position.adapter.AppHomeCordsAdapter
import breeze.emulate.position.bean.AppLocationBean
import breeze.emulate.position.bean.dao.AppLocationDao
import breeze.emulate.position.bean.impl.AppLocationDaoImpl
import breeze.emulate.position.tools.Hook
import breeze.lib.carnation.activity.OrchidBaseActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.logging.Level
import java.util.logging.Logger

class HomeActivity : OrchidBaseActivity() {

    private val TAG = "HomeActivity"

    private val REQUEST_ADD_POSITION = 100;
    private lateinit var materialToolbar: MaterialToolbar
    private var allDataInfo: List<AppLocationBean> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeCordsAdapter: AppHomeCordsAdapter
    private val appLocationDao:AppLocationDao = AppLocationDaoImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Logger.getGlobal().log(Level.INFO,"App start!!!!!!")

        materialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(materialToolbar)

        // check the module was launched
        val hooked = Hook.isHooked()
        if (!hooked) {
            materialToolbar.subtitle = "模块异常"
            AlertDialog.Builder(this)
                    .setTitle("注意")
                    .setMessage("模块未正常运行!")
                    .setCancelable(false)
                    .setPositiveButton("退出") { dialog, which ->
                        run {
                            finish()
                        }
                    }
                    .show()
            return
        }
        materialToolbar.subtitle = "模块正常"

        initializeAllViews()

        reflushView()

    }

    /**
     * show or dismiss the linearLayout
     */
    private fun reflushView() {
        // obtain all the data from the database
        Logger.getGlobal().log(Level.INFO,"读取数据库")
        allDataInfo = appLocationDao.findAll()
        homeCordsAdapter.update(allDataInfo)
        if (allDataInfo.isEmpty()) {
            findViewById<LinearLayout>(R.id.home_linearLayout).visibility = View.VISIBLE
            return
        }
        findViewById<LinearLayout>(R.id.home_linearLayout).visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun initializeAllViews() {
        homeCordsAdapter = AppHomeCordsAdapter(this, allDataInfo)
        recyclerView = findViewById(R.id.home_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = homeCordsAdapter
        homeCordsAdapter.onItemClickListsner = object :AppHomeCordsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, data: AppLocationBean) {
                RunningActivity.startActivity(this@HomeActivity,data.id)
            }
        }
        findViewById<FloatingActionButton>(R.id.home_floatButton).setOnClickListener {
            startActivityForResult(Intent(this, AddPositionActivity::class.java), REQUEST_ADD_POSITION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ADD_POSITION && resultCode == RESULT_OK) {
            reflushView()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}