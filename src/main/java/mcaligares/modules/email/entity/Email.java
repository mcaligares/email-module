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

package mcaligares.modules.email.entity;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import mcaligares.modules.email.exception.EmailException;
import mcaligares.modules.email.exception.InvalidEmailException;
import mcaligares.modules.email.exception.InvalidUsernameOrPasswordException;
import mcaligares.modules.email.utils.EmailUtils;

/**
 * 
 * @author miguel
 *
 */
public class Email {

    private Boolean debug;
    private String username;
    private String password;
    private String from;
    private String name;
    private String subject;
    private String body;
    private List<String> to;
    private List<Attachment> attaches;
    private String htmlFile;
    private String domain;
    private String folder;
    private Filter filter;
    private List<Message> messages;

    public Email(String username, String password, String from) throws EmailException, InvalidEmailException {
        this(username, password, from, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public Email(String username, String password, String from, String subject, String body) throws EmailException,
            InvalidEmailException {
        this(username, password, from, null, subject, body, null, null, null, null, null, null, null, null, null);
    }

    public Email(String username, String password, String from, String to, String subject, String body)
            throws EmailException, InvalidEmailException {
        this(username, password, from, null, subject, body, null, null, null, null, null, null, null, null, null);
        this.addTo(to);
    }

    public Email(String username, String password, String from, List<String> to, String subject, String body)
            throws EmailException, InvalidEmailException {
        this(username, password, from, to, subject, body, null, null, null, null, null, null, null, null, null);
    }

    private Email(String username, String password, String from, List<String> to, String subject, String body,
            String htmlMessage, String name, String hostName, Integer smtpPort, Boolean ssl, Boolean tls,
            Boolean debug, List<Attachment> attaches, String htmlFile) throws EmailException, InvalidEmailException {
        super();

        if (!EmailUtils.isValidEmail(username)) throw new InvalidEmailException("Invalid username " + username);

        if (!EmailUtils.isValidEmail(from)) throw new InvalidEmailException("Invalid email " + from);

        // TODO if password is really empty?
        if (EmailUtils.isBlank(password)) throw new InvalidUsernameOrPasswordException("password cannot be empty");

        this.to = to;
        this.from = from;
        this.name = name;
        this.body = body;
        this.subject = subject;
        this.username = username;
        this.password = password;
        this.attaches = attaches;
        this.htmlFile = htmlFile;
        this.debug = debug == null ? Boolean.FALSE : debug;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFrom() {
        return from;
    }

    public InternetAddress getFromAddress() throws AddressException, EmailException {
        if (!EmailUtils.isValidEmail(from)) { throw new InvalidEmailException("Invalid email " + from); }
        return new InternetAddress(from);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTo() {
        return to;
    }

    public InternetAddress[] getToAddresses() throws EmailException, AddressException {
        if (to == null || to.isEmpty()) { throw new EmailException("Destination address not configured"); }
        InternetAddress[] addresses = new InternetAddress[to.size()];
        for (int i = 0; i < to.size(); i++) {
            addresses[i] = new InternetAddress(to.get(i));
        }
        return addresses;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getDebug() {
        return debug;
    }

    public String getDebugStr() {
        return debug != null ? debug.toString() : Boolean.FALSE.toString();
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public List<Attachment> getAttaches() {
        return attaches;
    }

    public void setAttaches(List<Attachment> attaches) {
        this.attaches = attaches;
    }

    public String getHtmlFile() {
        return htmlFile;
    }

    public void setHtmlFile(String htmlFile) {
        this.htmlFile = htmlFile;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addTo(String address) {
        if (to == null) to = new ArrayList<String>();
        to.add(address);
    }

    public void addAttach(Attachment attach) {
        if (attaches == null) attaches = new ArrayList<Attachment>();
        attaches.add(attach);
    }

    public void addAttach(String name, String description, String path) {
        addAttach(new Attachment(name, description, path));
    }

    public void addMessage(Message message) {
        if (messages == null) messages = new ArrayList<Message>();
        messages.add(message);
    }

    public boolean hasHtmlFile() {
        return EmailUtils.isNotBlank(htmlFile);
    }

    public Authenticator getAuthenticator() throws EmailException {
        if (EmailUtils.isBlank(username) || EmailUtils.isBlank(password)) {
            //TODO if password is really empty?
            throw new InvalidUsernameOrPasswordException("username or password cannot be empty");
        }
        return new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
    }

    public String getDomain() {
        if (domain == null) domain = EmailUtils.getDomain(from);
        return domain;
    }

}
