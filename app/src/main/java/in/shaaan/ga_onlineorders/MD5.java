package in.shaaan.ga_onlineorders;

import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by S on 18-11-2017.
 */

public class MD5 {


    public static String md5(InputStream is) throws IOException {
        String md5 = "";

        try {
            byte[] bytes = new byte[4096];
            int read = 0;
            MessageDigest digest = MessageDigest.getInstance("MD5");

            while ((read = is.read(bytes)) != -1) {
                digest.update(bytes, 0, read);
            }

            byte[] messageDigest = digest.digest();

            String base64 = Base64.encodeToString(messageDigest, Base64.NO_WRAP);

            md5 = base64;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return md5;
    }
}
