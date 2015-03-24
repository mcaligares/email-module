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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import mcaligares.modules.email.exception.EmailException;
import mcaligares.modules.email.manager.EmailManager;
import mcaligares.modules.email.utils.EmailUtils;

/**
 * 
 * @author miguel
 *
 */
public final class Message {

    private String from;
    private String subject;
    private String content;
    private List<String> to;
    private List<File> attaches;
    private Exception ex;

    public Message() {
        super();
    }

    public Message(String from, String subject, String content, List<String> to, List<File> attaches) {
        super();
        this.from = from;
        this.subject = subject;
        this.content = content;
        this.to = to;
        this.attaches = attaches;
    }

    public Message(final javax.mail.Message message) throws Exception {
        if (message == null) throw new NullPointerException();

        try {
            // Get first from address
            // TODO only first?
            this.from = message.getFrom()[0].toString();

            // Get subject
            this.subject = message.getSubject();

            // Add address
            for (Address address : message.getReplyTo()) {
                addTo(address.toString());
            }

            // Get content
            Object content = message.getContent();

            if (content instanceof String) {
                this.content = (String) content;

            } else if (content instanceof Multipart) {
                // Get multipart
                Multipart messageContent = (Multipart) content;

                // Loop looking for message
                for (int i = 0; i < messageContent.getCount(); i++) {
                    BodyPart part = messageContent.getBodyPart(i);

                    if (MimeBodyPart.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        try {
                            addAttach(EmailUtils.createFileTemp(part.getInputStream(), part.getFileName()));
                        } catch (Exception e) {
                            this.ex = e;
                        }

                    } else if (MimeBodyPart.INLINE.equalsIgnoreCase(part.getDisposition())) {

                    } else {
                        Object object = part.getContent();

                        if (object instanceof String &&
                                part.getContentType().toLowerCase().startsWith(EmailManager.TEXT_MIME_TYPE)) {
                            this.content = (String) object;

                        } else if (object instanceof MimeMultipart) {
                            MimeMultipart multipart = (MimeMultipart) object;
                            for (int j = 0; j < multipart.getCount(); j++) {
                                BodyPart bp = multipart.getBodyPart(j);
                                if (bp.getContentType() != null &&
                                        bp.getContentType().toLowerCase().startsWith(EmailManager.TEXT_MIME_TYPE)) {
                                    this.content = (String) bp.getContent();
                                }
                            }
                        }
                    }
                }
            } else {
                throw new EmailException("Message type not found");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<File> getAttaches() {
        return attaches;
    }

    public void setAttaches(List<File> attaches) {
        this.attaches = attaches;
    }

    public Exception getError() {
        return ex;
    }

    public boolean hasError() {
        return ex != null;
    }

    public boolean hasAttaches() {
        return attaches != null && !attaches.isEmpty();
    }

    public void addTo(String address) {
        if (to == null) to = new ArrayList<String>();
        to.add(address);
    }

    public void addAttach(File file) {
        if (attaches == null) attaches = new ArrayList<File>();
        attaches.add(file);
    }

}
