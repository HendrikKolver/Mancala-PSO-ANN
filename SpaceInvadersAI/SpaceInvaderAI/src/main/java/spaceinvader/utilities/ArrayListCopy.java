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
        ArrayList<T> newList = new ArrayList();
        for(T item : list){
            newList.add((T)item.clone());
        }
        return newList;
    }

}
