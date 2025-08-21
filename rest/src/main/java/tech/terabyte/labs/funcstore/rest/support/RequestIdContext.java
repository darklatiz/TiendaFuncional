package tech.terabyte.labs.funcstore.rest.support;

public class RequestIdContext {

    private static final ThreadLocal<String> REQ_ID = new ThreadLocal<>();
    private RequestIdContext() {}
    public static void set(String id) { REQ_ID.set(id); }
    public static String get() { return REQ_ID.get(); }
    public static void clear() { REQ_ID.remove(); }

}
