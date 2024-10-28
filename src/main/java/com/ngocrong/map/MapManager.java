//package com.ngocrong.map;
//
//import org.apache.log4j.Logger;
//
//import java.util.HashMap;
//
//public class MapManager implements Runnable{
//    private static Logger logger = Logger.getLogger(MapManager.class);
//    private static MapManager instance;
//
//    public HashMap<Integer, TMap> maps;
////    public HashMap<Integer, IMap> list;
//
//    public boolean running;
//
//    public MapManager() {
//        this.running = true;
//        maps = new HashMap<>();
//        list = new HashMap<>();
//    }
//
//    public static MapManager getInstance() {
//        if (instance == null) {
//            synchronized (MapManager.class) {
//                if (instance == null) {
//                    instance = new MapManager();
//                }
//            }
//        }
//        return instance;
//    }
//
//    @Override
//    public void run() {
//
//    }
//}
