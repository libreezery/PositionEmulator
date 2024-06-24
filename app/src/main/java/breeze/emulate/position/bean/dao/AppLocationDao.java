package breeze.emulate.position.bean.dao;

import breeze.emulate.position.bean.AppLocationBean;

public interface AppLocationDao extends GeneralDao<AppLocationBean> {

    AppLocationBean findById(int id);

}
