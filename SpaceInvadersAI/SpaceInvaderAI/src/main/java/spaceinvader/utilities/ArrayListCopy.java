package spaceinvader.utilities;

import java.util.ArrayList;
import spaceinvader.entities.Building;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.Shield;

/**
 *
 * @author Hendrik Kolver
 */
public class ArrayListCopy {

    public static <T extends GameObject> ArrayList<T> copyArray(ArrayList<T> list){
        ArrayList<T> newList = new ArrayList();
        for(T item : list){
            newList.add((T)item.getCopy());
        }
        return newList;
    }

}
