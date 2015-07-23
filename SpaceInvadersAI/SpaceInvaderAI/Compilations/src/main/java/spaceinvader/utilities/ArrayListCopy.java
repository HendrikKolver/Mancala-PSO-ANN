package spaceinvader.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import spaceinvader.entities.GameObject;

/**
 *
 * @author Hendrik Kolver
 */
public class ArrayListCopy {

    public static <T extends GameObject> ArrayList<T> copyArray(ArrayList<T> list){
        Object[] array = list.toArray();
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).clone();
        }
        ArrayList<T> newList = new ArrayList(Arrays.asList(array));
        return newList;
    }

}
