/*
 * Copyright 2015 Miguel Augusto Caligares <mcaligares@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mcaligares.modules.email.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import mcaligares.modules.email.exception.EmailException;

/**
 * 
 * @author miguel
 *
 */
public final class EmailUtils {

    private static final String EMAIL_PATTERN = "^\\s*?(.+)@(.+?)\\s*$";

    public static boolean isEmpty(final CharSequence cs) {
         return cs == null || cs.length() < 1;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(final CharSequence cs) {
        if (isNotEmpty(cs)) {
            for (int i = 0; i < cs.length(); i++) {
                if (Character.isWhitespace(cs.charAt(i)) == false)
                    return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isValidEmail(final String email) {
        if (isNotEmpty(email)) {
            return email.matches(EMAIL_PATTERN);
        }
        return false;
    }

    public static String getDomain(final String email) {
        if (isValidEmail(email)) {
            return email.substring(email.indexOf("@") + 1);
        }
        return null;
    }

    public static String readFileAsString(final String filePath) throws EmailException {
        if (isEmpty(filePath) || isBlank(filePath)) return null;

        StringBuilder sb = null;
        BufferedReader reader = null;

        try {
            sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(filePath)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
 
        } catch (Exception e) {
            throw new EmailException(e);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                throw new EmailException(e);
            }
        }

        String fileAsString = sb.toString();
        return isNotBlank(fileAsString) ? fileAsString : null;
    }

    public static File createFileTemp(final InputStream input, final String name) throws EmailException {
        File file = null;
        OutputStream output = null;

        try {
            file = File.createTempFile(name, null);
            output = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }

        } catch(Exception e) {
            throw new EmailException("error creating temp file");
        } finally {
            try {
                if (input != null) input.close();
            } catch(Exception e) {}

            try {
                if (output != null) output.close();
            } catch(Exception e) {}
        }

        return file;
    }

}
