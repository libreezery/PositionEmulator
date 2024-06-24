package breeze.emulate.position.bean;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class AppCampusLocationData {

    @Deprecated
    public static List<CoordinateBean> getCampusCoordinate1() {
        List<CoordinateBean> objects = new ArrayList<>();
        objects.add(createCoordinate(104.68295666666667,28.816126666666666));
        objects.add(createCoordinate(104.68256166666667,28.816488333333336));
        objects.add(createCoordinate(104.68225499999998,28.81671833333333));
        objects.add(createCoordinate(104.68213833333333,28.816798333333335));
        objects.add(createCoordinate(104.68204499999999,28.816844999999997));
        objects.add(createCoordinate(104.68196166666667,28.816876666666666));
        objects.add(createCoordinate(104.68186166666668,28.816876666666666));
        objects.add(createCoordinate(104.68176333333334,28.81686666666667));
        objects.add(createCoordinate(104.68162666666667,28.816813333333336));
        objects.add(createCoordinate(104.68147166666668,28.816660000000002));
        objects.add(createCoordinate(104.68144166666667,28.816558333333333));
        objects.add(createCoordinate(104.68146666666668,28.816399999999998));
        objects.add(createCoordinate(104.68150666666666,28.816309999999998));
        objects.add(createCoordinate(104.68166166666667,28.816186666666667));
        objects.add(createCoordinate(104.68185333333334,28.81604666666666));
        objects.add(createCoordinate(104.68203166666666,28.815901666666665));
        objects.add(createCoordinate(104.68227333333334,28.815720000000002));
        objects.add(createCoordinate(104.68240666666667,28.815649999999998));
        objects.add(createCoordinate(104.68259166666667,28.815630000000002));
        objects.add(createCoordinate(104.68273333333333,28.81568666666667));
        objects.add(createCoordinate(104.68285333333333,28.815766666666665));
        objects.add(createCoordinate(104.68292500000001,28.815866666666672));
        objects.add(createCoordinate(104.68292166666667,28.816008333333336));
        objects.add(createCoordinate(104.68289666666666,28.816143333333336));
        return objects;
    }

    public static List<CoordinateBean> getCampusCoordinate() {
        List<CoordinateBean> objects = new ArrayList<>();
        objects.add(createCoordinate(104.68278333333335,28.81626833333333));
        objects.add(createCoordinate(104.68263833333334,28.816380000000002));
        objects.add(createCoordinate(104.68245499999999,28.816531666666666));
        objects.add(createCoordinate(104.68224166666666,28.81670166666667));
        objects.add(createCoordinate(104.68208333333332,28.816816666666668));
        objects.add(createCoordinate(104.68192499999999,28.81685166666667));
        objects.add(createCoordinate(104.68180499999998,28.816846666666667));
        objects.add(createCoordinate(104.68168333333332,28.816801666666667));
        objects.add(createCoordinate(104.68160000000002,28.81674333333334));
        objects.add(createCoordinate(104.68150666666666,28.816604999999996));
        objects.add(createCoordinate(104.68148666666666,28.816491666666668));
        objects.add(createCoordinate(104.68151000000002,28.81637333333333));
        objects.add(createCoordinate(104.68160333333331,28.816248333333338));
        objects.add(createCoordinate(104.68177166666668,28.816110000000002));
        objects.add(createCoordinate(104.68193333333332,28.815973333333332));
        objects.add(createCoordinate(104.68215833333333,28.815793333333332));
        objects.add(createCoordinate(104.68227166666665,28.815720000000002));
        objects.add(createCoordinate(104.68238333333332,28.81565666666667));
        objects.add(createCoordinate(104.68251000000001,28.815643333333334));
        objects.add(createCoordinate(104.68265166666667,28.81565666666667));
        objects.add(createCoordinate(104.68275166666666,28.815693333333336));
        objects.add(createCoordinate(104.68287333333335,28.81577333333333));
        objects.add(createCoordinate(104.68293666666665,28.815901666666665));
        objects.add(createCoordinate(104.68292833333335,28.816041666666667));
        objects.add(createCoordinate(104.68290499999999,28.816143333333336));
        objects.add(createCoordinate(104.68282833333333,28.81623666666667));
        return objects;
    }

    public static CoordinateBean createCoordinate(double lon, double lat) {
        CoordinateBean coordinateBean = new CoordinateBean();
        coordinateBean.longitude = lon;
        coordinateBean.latitude = lat;
        return coordinateBean;
    }
}
