package breeze.emulate.position.bean.dao;

import java.util.List;

public interface GeneralDao<T> {

    /**
     * 添加数据到数据库
     * @param bean
     * @return
     */
    boolean add(T bean);

    /**
     * 获取数据库中全部数据
     * @return
     */
    List<T> findAll();

    /**
     * 删除所有数据
     * @return
     */
    boolean deleteAll();

}
