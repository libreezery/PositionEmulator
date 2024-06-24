package breeze.emulate.position.bean.impl;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import breeze.emulate.position.bean.AppLocationBean;
import breeze.emulate.position.bean.dao.AppLocationDao;

public class AppLocationDaoImpl extends AppDatabaseDao implements AppLocationDao {

    private static Dao<AppLocationBean, Integer> DAO;

    public AppLocationDaoImpl(Context context) {
        super(context);
        try {
            DAO = HELPER.getDao(AppLocationBean.class);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean add(AppLocationBean bean) {
        assert DAO != null;
        try {
            return DAO.create(bean) > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public List<AppLocationBean> findAll() {
        try {
            return DAO.queryForAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new ArrayList<>();
        }
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
    public AppLocationBean findById(int id) {
        try {
            return DAO.queryForId(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}
