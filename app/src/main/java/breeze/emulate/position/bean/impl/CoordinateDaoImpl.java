package breeze.emulate.position.bean.impl;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import breeze.emulate.position.bean.AppLocationBean;
import breeze.emulate.position.bean.CoordinateBean;
import breeze.emulate.position.bean.dao.CoordinateDao;

public class CoordinateDaoImpl extends AppDatabaseDao implements CoordinateDao {

    private static Dao<CoordinateBean,Integer> DAO;

    public CoordinateDaoImpl(Context context) {
        super(context);
        try {
            DAO = HELPER.getDao(CoordinateBean.class);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean add(CoordinateBean bean) {
        try {
            return DAO.create(bean)>0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public List<CoordinateBean> findAll() {
        return null;
    }

    @Override
    public boolean deleteAll() {
        try {
            return DAO.deleteBuilder().delete()>0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public List<CoordinateBean> findForAppLocation(AppLocationBean bean) {
        try {
            return DAO.queryBuilder().where().eq(CoordinateBean.TABLE_APP_LOCATION,bean.id).query();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new ArrayList<>();
        }
    }
}
