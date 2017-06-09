package sample;

import java.util.HashMap;

/**
 * Created by you on 2017/05/26.
 */
public class Type {
    static double[][] aff;
    static HashMap type;

    public Type(){
        this.type = new HashMap<String, Integer>();
        type.put("RED",0);
        type.put("BLUE",1);
        type.put("GREEN",2);
        type.put("WHITE",3);
        type.put("BLACK",4);
        this.aff = new double[][]{{1, 0.5, 2, 0.5, 2}, {2, 1, 0.5, 0.5, 2}, {0.5, 2, 1, 0.5, 2}, {0.5, 0.5, 0.5, 1, 1}, {1.5, 1.5, 1.5, 1, 2}};
    }

    public double typeaff(String atktype, String dmgtype){
        return aff[(int) type.get(atktype)][(int) type.get(dmgtype)];
    }
}
