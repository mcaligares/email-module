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

package mcaligares.modules.email.manager;

import java.util.Properties;

import mcaligares.modules.email.entity.Email;
import mcaligares.modules.email.exception.EmailException;

/**
 * 
 * @author miguel
 *
 */
public interface EmailManager {

    public static final String MAIL_STORE_PROTOCOL_IMAPS = "imaps";
    public static final String MAIL_TRANSPORT_PROTOCOL_SMTP = "smtp";

    public static final String MAIL_DEBUG = "mail.debug";
    public static final String MAIL_STORE_PROTOCOL = "mail.store.protocol";
    public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";

    public static final String MAIL_SMTP_USER = "mail.smtp.user";
    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String MAIL_SMTP_FROM = "mail.smtp.from";
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
    public static final String MAIL_SMTP_SENDPARTIAL = "mail.smtp.sendpartial";
    public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String MAIL_SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
    public static final String MAIL_SMTP_CONNECTIONTIMEOUT = "mail.smtp.connectiontimeout";

    public static final String MAIL_IMAP_USER = "mail.imap.user";
    public static final String MAIL_IMAP_HOST = "mail.imap.host";
    public static final String MAIL_IMAP_PORT = "mail.imap.port";
    public static final String MAIL_IMAP_FROM = "mail.imap.from";
    public static final String MAIL_IMAP_AUTH = "mail.imap.auth";
    public static final String MAIL_IMAP_TIMEOUT = "mail.imap.timeout";
    public static final String MAIL_IMAP_SENDPARTIAL = "mail.imap.sendpartial";
    public static final String MAIL_IMAP_STARTTLS_ENABLE = "mail.imap.starttls.enable";
    public static final String MAIL_IMAP_STARTTLS_REQUIRED = "mail.imap.starttls.required";
    public static final String MAIL_IMAP_CONNECTIONTIMEOUT = "mail.imap.connectiontimeout";

    public static final String HTML_MIME_TYPE = "text/html";
    public static final String TEXT_MIME_TYPE = "text/plain";
    public static final String DEFAULT_MAIL_FOLDER = "INBOX";

    public void send(final Email email) throws EmailException;

    public void receive(final Email email) throws EmailException;

    public void loadProperties(final Properties properties);

}
