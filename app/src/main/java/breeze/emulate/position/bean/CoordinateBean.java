package breeze.emulate.position.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_coordinate")
public class CoordinateBean {
    public static final String TABLE_APP_LOCATION = "app_location_bean";

    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(columnName = TABLE_APP_LOCATION, foreign = true)
    public AppLocationBean appLocationBean;
    @DatabaseField
    public double longitude;
    @DatabaseField
    public double latitude;
}
