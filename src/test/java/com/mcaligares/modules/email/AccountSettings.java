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

package com.mcaligares.modules.email;

import mcaligares.modules.email.exception.EmailException;

/**
 * Interface to test
 * 
 * @author miguel
 *
 */
public interface AccountSettings {

    static final String GMAIL_USER = "xxxxxx@gmail.com";
    static final String GMAIL_PASS = "xxxxxx";

    static final String YAHOO_USER = "xxxxxx@yahoo.com";
    static final String YAHOO_PASS = "xxxxxx";

    static final String HOTMAIL_USER = "xxxxxx@hotmail.com";
    static final String HOTMAIL_PASS = "xxxxxx";

    static final String SUBJECT_SIMPLE = "Test sending a simple email";
    static final String SUBJECT_TEST_SIMPLE = "Testing email receive";
    static final String SUBJECT_HTML = "Test sending a html file as email";
    static final String SUBJECT_ATTACH = "Test sending a email with attachment";

    void receive() throws EmailException;

    void sendSimpleText() throws EmailException;

    void sendHtmlFile() throws EmailException;

    void sendAttachment() throws EmailException;

}
