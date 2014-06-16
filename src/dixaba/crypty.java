/* 
 * Copyright (c) 2014, DirectoriX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package dixaba;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DirectoriX
 */
public class crypty {

    private static final String syms = "0123456789aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ";
    private static final int slength = 62;

    private static String bytes2str(byte[] arr) {
        String result = "";
        int a = 0;
        for (int i = 0; i < arr.length; i++) {
            a += (arr[i]>=0)?arr[i]:arr[i]+256;
        }

        while (a > 0) {
            int x = a % slength;
            result += syms.charAt(x);
            a -= result.length();
        }

        return result;
    }

    private static byte[] str2bytes(String str) {
        byte[] result = new byte[str.length()];
        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }

        return result;
    }

    public static String remix(String str, int seed, int offset) {
        String result = "";

        int strlen = str.length();

        LinkedList<Character> list = new LinkedList<>();
        for (int i = 0; i < strlen; i++) {
            list.add(i, str.charAt(i));
        }

        int x = seed + offset;

        for (int i = strlen; i > 0; i--) {
            int a = x % list.size();
            result += list.get((a >= 0) ? a : (list.size() + a));
            list.remove((a >= 0) ? a : (list.size() + a));
            x = a + seed;
        }
        return result;
    }

    public static String hash(int algorithm, String str) throws NoSuchAlgorithmException {
        MessageDigest hasher;

        switch (algorithm) {
            case (1): {
                hasher = MessageDigest.getInstance("MD5");

                break;
            }
            case (2): {
                hasher = MessageDigest.getInstance("SHA-1");

                break;
            }
            case (3): {
                hasher = MessageDigest.getInstance("SHA-256");

                break;
            }
            default: {
                hasher = MessageDigest.getInstance("SHA-512");

                break;
            }
        }

        return bytes2str(hasher.digest(str2bytes(str)));
    }

    public static String crypt(int algorithm, String str, int maxlength, int seed) {
        String result;

        int strlen = str.length();

        int offset = maxlength % (seed + strlen + slength) + algorithm;
        result = remix(str, seed, offset);
        try {
            result = hash(algorithm, result);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(crypty.class.getName()).log(Level.SEVERE, null, ex);
        }

        int seed2 = result.length() + (algorithm * seed) % (maxlength + strlen + slength) + seed;
        int offset2 = strlen + offset % (algorithm + result.length()) + seed;

        result = remix(result, seed2, offset2);

        return (maxlength != 0) ? result.substring(0, maxlength) : result;
    }
}
