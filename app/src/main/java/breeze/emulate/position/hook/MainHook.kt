package breeze.emulate.position.hook

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Criteria
import android.location.LocationManager
import android.net.Uri
import android.telephony.gsm.GsmCellLocation
import android.util.Log
import breeze.emulate.position.bean.CoordinateBean
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainHook : IXposedHookLoadPackage {

    private lateinit var context: Context
    private var isRun = false
    private val TAG = "MainHook"
    private var positionData: CoordinateBean? = null
    private var id = 0
    private val coordinateBeans: MutableList<CoordinateBean> = ArrayList()
    // 创建一个只有一个线程的线程池
    private val executors:ExecutorService = Executors.newFixedThreadPool(1)

    override fun handleLoadPackage(p0: XC_LoadPackage.LoadPackageParam?) {
        if (p0 != null) {
            // hook self
            if (p0.packageName.equals("breeze.emulate.position")) {
                hookSelf(p0)
            }
            getContext(p0)
            // 修改 Location 类
            hookLocationClass(p0)
            // 修改 TelephonyManager 类
            hookLocationClass(p0)
        }
    }

    private fun hookTelephonyClass(p0: XC_LoadPackage.LoadPackageParam?) {
        GsmCellLocation().apply {
            
        }
    }

    private fun hookLocationClass(p0: XC_LoadPackage.LoadPackageParam?) {
        // Hook数据
        XposedHelpers.findAndHookMethod("android.location.Location", p0?.classLoader, "getLongitude", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                if (isRun && positionData != null) {
                    param.result = positionData!!.longitude
                }
            }
        })
        XposedHelpers.findAndHookMethod("android.location.Location", p0?.classLoader, "getLatitude", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                if (isRun && positionData != null) {
                    param.result = positionData!!.latitude
                }
            }
        })
    }

    /**
     * self hook for check module was enabled
     */
    private fun hookSelf(p0: XC_LoadPackage.LoadPackageParam?) {
        XposedHelpers.findAndHookMethod(
                "breeze.emulate.position.tools.Hook",
                p0?.classLoader,
                "isHooked",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        param?.result = true
                        super.afterHookedMethod(param)
                    }
                })
    }

    private fun getContext(p0: XC_LoadPackage.LoadPackageParam?) {
        // 获取Context
        XposedHelpers.findAndHookMethod(Application::class.java, "attach", Context::class.java, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)
                context = param.args[0] as Context
                val intentFilter = IntentFilter()
                intentFilter.addAction("breeze.emulate.position.hook.UPDATE_SETTINGS")
                intentFilter.addAction("breeze.emulate.position.hook.UPDATE_COORDINATES")
                context.registerReceiver(AppSettingsChangeReceiver(), intentFilter)
            }
        })
    }

    private fun getCoordinates() {
        // 获取坐标
        coordinateBeans.clear()
        val parse = Uri.parse("content://breeze.emulate.position.provider/coordinate")
        val contentResolver = context.contentResolver
        @SuppressLint("Recycle") val query = contentResolver.query(parse, null, "app_location_bean = ?", arrayOf("$id"), null)
        if (query != null && query.moveToFirst()) {
            do {
                val coordinateBean = CoordinateBean()
                val longitude = query.getDouble(query.getColumnIndex("longitude"))
                val latitude = query.getDouble(query.getColumnIndex("latitude"))
                coordinateBean.latitude = latitude
                coordinateBean.longitude = longitude
                coordinateBeans.add(coordinateBean)
            } while (query.moveToNext())
            XposedBridge.log("读取坐标数据${coordinateBeans.size}个")
        } else {
            Log.i(TAG, "getCoordinates: 没有获取到数据库")
        }
    }

    inner class AppSettingsChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if ("breeze.emulate.position.hook.UPDATE_SETTINGS" == intent.action) {
                isRun = intent.getBooleanExtra("isRun", false)
                id = intent.getIntExtra("id", 0)
                val speed = intent.getIntExtra("speedMillion", 10) * 1000
                Log.i(TAG, "onReceive: 是否运行:$isRun|运行速度:$speed")
                if (isRun) {
                    getCoordinates()
                    if (coordinateBeans.size > 0) {
                        executors.execute {
                            var target = 0
                            while (isRun) {
                                positionData = coordinateBeans[target]
                                XposedBridge.log("数据更新:" + positionData?.longitude + "||" + positionData?.latitude)
                                if (target == coordinateBeans.size - 1) {
                                    target = 0
                                } else {
                                    target++
                                }
                                Thread.sleep(speed.toLong())
                            }
                        }
                    }
                }
            }
        }
    }
}