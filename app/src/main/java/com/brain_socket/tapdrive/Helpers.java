package com.brain_socket.tapdrive;

/**
 * Created by Albert on 7/8/17.
 */
public class Helpers {
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
