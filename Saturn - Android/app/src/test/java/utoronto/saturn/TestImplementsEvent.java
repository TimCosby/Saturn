package utoronto.saturn;

import org.junit.Assert;
import org.junit.Test;

public class TestImplementsEvent {
    private static final Event LE = new Event(null, null, null, 0, 0, null, 0);
    private static final LocalEventManager LEM = new LocalEventManager();

    private static void assertImplementsOrExtends(Object instObj, Class<?> obj) {
        try {
            Class<?> test = Class.forName(instObj.getClass().getCanonicalName());
        } catch(ClassNotFoundException e) {
            Assert.fail(instObj.getClass().getSimpleName() + " does not implement " + obj.getSimpleName() + "!");
        }
    }

    private static void assertImplementsMethod(Object obj, Class[] cArgs, String method) {
        try {
            obj.getClass().getMethod(method, cArgs);
        } catch(NoSuchMethodException e) {
            Assert.fail(obj.getClass().getSimpleName() + " does not implement " + method + "!");
        }
    }

    @Test
    public void testLocalEventManagerExtendsEventManager() {
        assertImplementsOrExtends(LEM, EventManager.class);
    }
}