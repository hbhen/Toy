//package toy.android.com.toy.activity;
//
//import org.json.JSONObject;
//
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
///**
// * Created by Android on 2017/8/25.
// */
//
//public class ListenerManager {
//
//    public static ListenerManager listenerManager;
//    public List<IListener> list = new CopyOnWriteArrayList<>();
//
//    public static ListenerManager getInstance() {
//        if (listenerManager == null) {
//            listenerManager = new ListenerManager();
//        }
//        return listenerManager;
//    }
//
//    public void registerListtener(IListener iListener) {
//        list.add(iListener);
//    }
//
//    public void unRegisterListener(IListener iListener) {
//        if (list.contains(iListener)) {
//            list.remove(iListener);
//        }
//    }
//
//    public void sendBroadCast(String cmd, JSONObject object) {
//        for (IListener iListener : list) {
//            iListener.notifyAllActivity(cmd, object);
//        }
//    }
//
//    public interface IListener {
//        void notifyAllActivity(String s, JSONObject object);
//    }
//}
