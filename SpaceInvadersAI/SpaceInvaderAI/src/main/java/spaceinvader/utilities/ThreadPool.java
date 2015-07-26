package spaceinvader.utilities;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Hendrik Kolver
 */
public class ThreadPool {
    public static ExecutorService executor = Executors.newFixedThreadPool(8);
}
