package breeze.emulate.position.bean.dao;

import java.util.List;

import breeze.emulate.position.bean.AppLocationBean;
import breeze.emulate.position.bean.CoordinateBean;

public interface CoordinateDao extends GeneralDao<CoordinateBean> {
    List<CoordinateBean> findForAppLocation(AppLocationBean bean);
}
