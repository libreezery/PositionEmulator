package breeze.emulate.position.bean;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_app_location")
public class AppLocationBean {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    public String title;
    @DatabaseField
    public long   createTime;
    @DatabaseField
    public String            description;
    @ForeignCollectionField
    public ForeignCollection<CoordinateBean> coordinates;
}
