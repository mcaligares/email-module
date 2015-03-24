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

package mcaligares.modules.email.manager.impl;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import mcaligares.modules.email.entity.Attachment;
import mcaligares.modules.email.entity.Email;
import mcaligares.modules.email.exception.EmailException;
import mcaligares.modules.email.exception.EmailServerNotFoundException;
import mcaligares.modules.email.exception.InvalidUsernameOrPasswordException;
import mcaligares.modules.email.manager.EmailManager;
import mcaligares.modules.email.utils.EmailUtils;
import mcaligares.modules.properties.annotation.BeanProperties;
import mcaligares.modules.properties.annotation.Key;
import mcaligares.modules.properties.config.PropertiesConfig;
import mcaligares.modules.properties.utils.PropertiesBuilder;

/**
 * 
 * @author miguel
 *
 */
@BeanProperties(prop = "mcaligares/modules/email/config.properties")
public class EmailManagerImpl implements EmailManager {

    private static @Key("email.domain.gmail") String DOMAIN_GMAIL;
    private static @Key("email.domain.yahoo") String DOMAIN_YAHOO;
    private static @Key("email.domain.hotmail") String DOMAIN_HOTMAIL;
    private static @Key("email.domain.outlook") String DOMAIN_OUTLOOK;

    private static @Key("email.smtp.server.gmail") String SMTP_SERVER_GMAIL;
    private static @Key("email.smtp.server.yahoo") String SMTP_SERVER_YAHOO;
    private static @Key("email.smtp.server.hotmail") String SMTP_SERVER_HOTMAIL;

    private static @Key("email.smtp.port.gmail") Integer SMTP_PORT_GMAIL;
    private static @Key("email.smtp.port.yahoo") Integer SMTP_PORT_YAHOO;
    private static @Key("email.smtp.port.hotmail") Integer SMTP_PORT_HOTMAIL;

    private static @Key("email.imap.server.gmail") String IMAP_SERVER_GMAIL;
    private static @Key("email.imap.server.yahoo") String IMAP_SERVER_YAHOO;
    private static @Key("email.imap.server.hotmail") String IMAP_SERVER_HOTMAIL;

    private static @Key("email.smtp.timeout") Integer SMTP_TIMEOUT;
    private static @Key("email.smtp.connectiontimeout") Integer SMTP_CONNECTION_TIMEOUT;

    private static @Key("email.imap.timeout") Integer IMAP_TIMEOUT;
    private static @Key("email.imap.connectiontimeout") Integer IMAP_CONNECTION_TIMEOUT;

    private static Properties emailProperties;
    private static final String EMAIL_PROPERTIES_FILE = "email.properties";

    static {
        //load config properties
        PropertiesConfig.build(new EmailManagerImpl());

        //load email properties 
        try {
            emailProperties = PropertiesBuilder.createFromFile(EMAIL_PROPERTIES_FILE);
        } catch(Exception e) {}

        if (emailProperties == null) {
            emailProperties = new Properties();
        }
    }

    public EmailManagerImpl() {
        super();
    }

    public void send(final Email email) throws EmailException {
        // Checking
        if (email == null)
            throw new NullPointerException("email object is null");

        if (EmailUtils.isBlank(email.getUsername()))
            throw new InvalidUsernameOrPasswordException("username cannot be empty");

        if (EmailUtils.isBlank(email.getPassword()))
            throw new InvalidUsernameOrPasswordException("password cannot be empty");

        if (!EmailUtils.isValidEmail(email.getFrom()))
            throw new EmailException("Invalid email " + email.getFrom());

        if (email.getTo() == null || email.getTo().isEmpty())
            throw new EmailException("Empty destination emails");

        if (EmailUtils.isBlank(email.getSubject()))
            throw new EmailException("Subject cannot be empty");

        try {
            // Checking send properties
            checkSendProperties(email);

            // Get instance of Session
            final Session session = Session.getInstance(emailProperties, email.getAuthenticator());

            // Create a default MimeMessage object.
            final MimeMessage message = new MimeMessage(session);

            // Create a multipar message
            final Multipart multipart = new MimeMultipart();

            // Create a transport
            final Transport transport = session.getTransport();

            // Set Subject: header field
            message.setSubject(email.getSubject());

            // Set From: header field of the header.
            message.setFrom(email.getFromAddress());

            // Set To: header field of the header.
            message.addRecipients(Message.RecipientType.TO, email.getToAddresses());

            // Create the message part 
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            if (email.hasHtmlFile()) {
                messageBodyPart.setContent(
                        EmailUtils.readFileAsString(email.getHtmlFile()), HTML_MIME_TYPE);
            } else {
                messageBodyPart.setText(email.getMessage());
            }

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            if (email.getAttaches() != null && !email.getAttaches().isEmpty()) {
                for (Attachment attach : email.getAttaches()) {
                    messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setDataHandler(
                            new DataHandler(ClassLoader.getSystemResource(attach.getPath())));
                    messageBodyPart.setFileName(attach.getName());
                    multipart.addBodyPart(messageBodyPart);
                }
            }

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            transport.connect();
            transport.sendMessage(message, email.getToAddresses());
            transport.close();

        } catch(EmailException e) {
            throw e;
        } catch(Exception e) {
            throw new EmailException(e);
        }
    }

    public void receive(final Email email) throws EmailException {
        // Checking
        if (email == null)
            throw new NullPointerException("email object cannot be null");

        if (EmailUtils.isBlank(email.getUsername()))
            throw new InvalidUsernameOrPasswordException("username cannot be empty");

        if (EmailUtils.isBlank(email.getPassword()))
            throw new InvalidUsernameOrPasswordException("password cannot be empty");

        if (!EmailUtils.isValidEmail(email.getFrom()))
            throw new EmailException("Invalid email " + email.getFrom());

        try {
            // Check receive properties
            checkReceiveProperties(email);

            // Get instance of Session
            final Session session = Session.getInstance(emailProperties, email.getAuthenticator());

            // Create store from session
            final Store store = session.getStore();

            // Connect store to server
            store.connect(getImapServer(email.getDomain()), email.getUsername(), email.getPassword());

            // Get folder
            final Folder folder = store.getFolder(EmailUtils.isNotBlank(email.getFolder())
                    ? email.getFolder() : DEFAULT_MAIL_FOLDER);

            // Open folder
            folder.open(Folder.READ_ONLY);

            // Get messages from folder
            final Message[] messages = email.getFilter() != null
                    ? folder.search(email.getFilter().getSearchTerm()) : folder.getMessages();

            if (messages != null) {
                for (int i = 0; i < messages.length; i++) {
                    Message message = messages[i];
                    email.addMessage(new mcaligares.modules.email.entity.Message(message));
                }
            }

            // Close folder
            folder.close(false);

            // Close store
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadProperties(final Properties properties) {
        if (properties != null) emailProperties = properties;
    }

    private String getImapServer(final String domain) throws EmailException {
        if (EmailUtils.isNotBlank(domain)) {
            if (domain.toUpperCase().contains(DOMAIN_GMAIL.toUpperCase())) {
                return IMAP_SERVER_GMAIL;
            } else if (domain.toUpperCase().contains(DOMAIN_YAHOO.toUpperCase())) {
                return IMAP_SERVER_YAHOO;
            } else if (domain.toUpperCase().contains(DOMAIN_HOTMAIL.toUpperCase())
                    || domain.toUpperCase().contains(DOMAIN_OUTLOOK.toUpperCase())) {
                return IMAP_SERVER_HOTMAIL;
            }
        }
        throw new EmailServerNotFoundException("not smtp server found");
    }

    private String getSmtpServer(final String domain) throws EmailException {
        if (EmailUtils.isNotBlank(domain)) {
            if (domain.toUpperCase().contains(DOMAIN_GMAIL.toUpperCase())) {
                return SMTP_SERVER_GMAIL;
            } else if (domain.toUpperCase().contains(DOMAIN_YAHOO.toUpperCase())) {
                return SMTP_SERVER_YAHOO;
            } else if (domain.toUpperCase().contains(DOMAIN_HOTMAIL.toUpperCase())
                    || domain.toUpperCase().contains(DOMAIN_OUTLOOK.toUpperCase())) {
                return SMTP_SERVER_HOTMAIL;
            }
        }
        throw new EmailServerNotFoundException("not smtp server found");
    }

    private Integer getSmtpPort(final String domain) throws EmailException {
        if (EmailUtils.isNotBlank(domain)) {
            if (domain.toUpperCase().contains(DOMAIN_GMAIL.toUpperCase())) {
                return SMTP_PORT_GMAIL;
            } else if (domain.toUpperCase().contains(DOMAIN_YAHOO.toUpperCase())) {
                return SMTP_PORT_YAHOO;
            } else if (domain.toUpperCase().contains(DOMAIN_HOTMAIL.toUpperCase())
                    || domain.toUpperCase().contains(DOMAIN_OUTLOOK.toUpperCase())) {
                return SMTP_PORT_HOTMAIL;
            }
        }
        throw new EmailServerNotFoundException("not smtp port found");
    }

    private Properties checkSendProperties(final Email email) throws EmailException {

        if (!email.getDebugStr().equalsIgnoreCase(emailProperties.getProperty(MAIL_DEBUG))) {
            emailProperties.setProperty(MAIL_DEBUG, email.getDebugStr());
        }

        if (!MAIL_TRANSPORT_PROTOCOL_SMTP.equalsIgnoreCase(emailProperties.getProperty(MAIL_TRANSPORT_PROTOCOL))) {
            emailProperties.setProperty(MAIL_TRANSPORT_PROTOCOL, MAIL_TRANSPORT_PROTOCOL_SMTP);
        }

        if (!email.getUsername().equalsIgnoreCase(emailProperties.getProperty(MAIL_SMTP_USER))) {
            emailProperties.setProperty(MAIL_SMTP_USER, email.getUsername());
        }

        if (!getSmtpServer(email.getDomain()).equalsIgnoreCase(emailProperties.getProperty(MAIL_SMTP_HOST))) {
            emailProperties.setProperty(MAIL_SMTP_HOST, getSmtpServer(email.getDomain()));
        }

        if (!getSmtpPort(email.getDomain()).toString().equalsIgnoreCase(emailProperties.getProperty(MAIL_SMTP_PORT))) {
            emailProperties.setProperty(MAIL_SMTP_PORT, getSmtpPort(email.getDomain()).toString());
        }

        if (!email.getFrom().equalsIgnoreCase(emailProperties.getProperty(MAIL_SMTP_FROM))) {
            emailProperties.setProperty(MAIL_SMTP_FROM, email.getFrom());
        }

        if (!Boolean.TRUE.toString().equalsIgnoreCase(emailProperties.getProperty(MAIL_SMTP_AUTH))) {
            emailProperties.setProperty(MAIL_SMTP_AUTH, Boolean.TRUE.toString());
        }

        if (!Boolean.TRUE.toString().equalsIgnoreCase(emailProperties.getProperty(MAIL_SMTP_SENDPARTIAL))) {
            emailProperties.setProperty(MAIL_SMTP_SENDPARTIAL, Boolean.TRUE.toString());
        }

        if (!Boolean.TRUE.toString().equalsIgnoreCase(emailProperties.getProperty(MAIL_SMTP_STARTTLS_ENABLE))) {
            emailProperties.setProperty(MAIL_SMTP_STARTTLS_ENABLE, Boolean.TRUE.toString());
        }

        if (!Boolean.TRUE.toString().equalsIgnoreCase(emailProperties.getProperty(MAIL_SMTP_STARTTLS_REQUIRED))) {
            emailProperties.setProperty(MAIL_SMTP_STARTTLS_REQUIRED, Boolean.TRUE.toString());
        }

        if (SMTP_TIMEOUT != null && !SMTP_TIMEOUT.toString()
                .equalsIgnoreCase(emailProperties.getProperty(MAIL_SMTP_TIMEOUT))) {
            emailProperties.setProperty(MAIL_SMTP_TIMEOUT, SMTP_TIMEOUT.toString());
        }

        if (SMTP_CONNECTION_TIMEOUT != null && !SMTP_CONNECTION_TIMEOUT.toString()
                .equalsIgnoreCase(emailProperties.getProperty(MAIL_SMTP_CONNECTIONTIMEOUT))) {
            emailProperties.setProperty(MAIL_SMTP_CONNECTIONTIMEOUT, SMTP_CONNECTION_TIMEOUT.toString());
        }

        return emailProperties;
    }

    private Properties checkReceiveProperties(final Email email) throws EmailException {

        if (!email.getDebugStr().equalsIgnoreCase(emailProperties.getProperty(MAIL_DEBUG))) {
            emailProperties.setProperty(MAIL_DEBUG, email.getDebugStr());
        }

        if(!MAIL_STORE_PROTOCOL_IMAPS.equalsIgnoreCase(emailProperties.getProperty(MAIL_STORE_PROTOCOL))) {
            emailProperties.setProperty(MAIL_STORE_PROTOCOL, MAIL_STORE_PROTOCOL_IMAPS);
        }

        if (!email.getUsername().equalsIgnoreCase(emailProperties.getProperty(MAIL_IMAP_USER))) {
            emailProperties.setProperty(MAIL_IMAP_USER, email.getUsername());
        }

        if (!getImapServer(email.getDomain()).equalsIgnoreCase(emailProperties.getProperty(MAIL_IMAP_HOST))) {
            emailProperties.setProperty(MAIL_IMAP_HOST, getImapServer(email.getDomain()));
        }

        //if (!getImapPort(email.getDomain()).toString().equalsIgnoreCase(EMAIL_PROPERTIES.getProperty(MAIL_IMAP_PORT))) {
        //    EMAIL_PROPERTIES.setProperty(MAIL_IMAP_PORT, getSmtpPort(email.getDomain()).toString());
        //}

        if (!email.getFrom().equalsIgnoreCase(emailProperties.getProperty(MAIL_IMAP_FROM))) {
            emailProperties.setProperty(MAIL_IMAP_FROM, email.getFrom());
        }

        /*if (!Boolean.TRUE.toString().equalsIgnoreCase(EMAIL_PROPERTIES.getProperty(MAIL_IMAP_AUTH))) {
            EMAIL_PROPERTIES.setProperty(MAIL_IMAP_AUTH, Boolean.TRUE.toString());
        }*/

        /*if (!Boolean.TRUE.toString().equalsIgnoreCase(EMAIL_PROPERTIES.getProperty(MAIL_IMAP_SENDPARTIAL))) {
            EMAIL_PROPERTIES.setProperty(MAIL_IMAP_SENDPARTIAL, Boolean.TRUE.toString());
        }*/

        /*if (!Boolean.TRUE.toString().equalsIgnoreCase(EMAIL_PROPERTIES.getProperty(MAIL_IMAP_STARTTLS_ENABLE))) {
            EMAIL_PROPERTIES.setProperty(MAIL_IMAP_STARTTLS_ENABLE, Boolean.TRUE.toString());
        }*/

        /*if (!Boolean.TRUE.toString().equalsIgnoreCase(EMAIL_PROPERTIES.getProperty(MAIL_IMAP_STARTTLS_REQUIRED))) {
            EMAIL_PROPERTIES.setProperty(MAIL_IMAP_STARTTLS_REQUIRED, Boolean.TRUE.toString());
        }*/

        if (IMAP_TIMEOUT != null && !IMAP_TIMEOUT.toString()
                .equalsIgnoreCase(emailProperties.getProperty(MAIL_IMAP_TIMEOUT))) {
            emailProperties.setProperty(MAIL_IMAP_TIMEOUT, IMAP_TIMEOUT.toString());
        }

        if (IMAP_CONNECTION_TIMEOUT != null && !IMAP_CONNECTION_TIMEOUT.toString()
                .equalsIgnoreCase(emailProperties.getProperty(MAIL_IMAP_CONNECTIONTIMEOUT))) {
            emailProperties.setProperty(MAIL_IMAP_CONNECTIONTIMEOUT, IMAP_CONNECTION_TIMEOUT.toString());
        }

        return emailProperties;
    }

}
