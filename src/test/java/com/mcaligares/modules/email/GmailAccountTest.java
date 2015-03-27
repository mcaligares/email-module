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
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import mcaligares.modules.email.entity.Email;
import mcaligares.modules.email.entity.Filter;
import mcaligares.modules.email.entity.Filter.FilterType;
import mcaligares.modules.email.exception.EmailException;
import mcaligares.modules.email.manager.EmailManager;
import mcaligares.modules.email.manager.impl.EmailManagerImpl;

import org.junit.Test;

/**
 * 
 * @author miguel
 *
 */
public class GmailAccountTest implements AccountSettings {

    static final EmailManager emailManager = new EmailManagerImpl();

    @Test
    public void receive() throws EmailException {
        // Creating email entity
        Email email = new Email(GMAIL_USER, GMAIL_PASS, GMAIL_USER);
        assertThat(email, notNullValue());

        // Setting filter to get emails
        email.setFilter(new Filter(SUBJECT_TEST_SIMPLE, FilterType.SUBJECT));

        // Getting emails
        emailManager.receive(email);

        // Get messages size
        int count = email.getMessages() != null ? email.getMessages().size() : 0;

        // Settings destination, subject and body
        email.addTo(GMAIL_USER);
        email.setSubject(SUBJECT_TEST_SIMPLE);
        email.setBody("this is a simple plain text");

        // Send email
        emailManager.send(email);

        // Getting emails
        emailManager.receive(email);
        assertThat(email.getMessages(), notNullValue());
        assertThat(email.getMessages().size(), is(count + 1));

    }

    @Test
    public void sendSimpleText() throws EmailException {
        //Creating email entity
        Email email = new Email(GMAIL_USER, GMAIL_PASS, GMAIL_USER,
                GMAIL_USER, SUBJECT_SIMPLE, "this is a simple plain text");
        assertThat(email, notNullValue());

        // Setting filter to get emails
        email.setFilter(new Filter(SUBJECT_SIMPLE, FilterType.SUBJECT));

        // Getting emails
        emailManager.receive(email);

        // Get messages size
        int count = email.getMessages() != null ? email.getMessages().size() : 0;

        // Send email
        emailManager.send(email);

        // Getting email
        emailManager.receive(email);
        assertThat(email.getMessages(), notNullValue());
        assertThat(email.getMessages().size(), is(count + 1));

    }

    @Test
    public void sendHtmlFile() throws EmailException {
        //Creating email entity
        Email email = new Email(GMAIL_USER, GMAIL_PASS, GMAIL_USER,
                GMAIL_USER, SUBJECT_HTML, "this message is override by template email");
        assertThat(email, notNullValue());

        // Setting filter to get emails
        email.setFilter(new Filter(SUBJECT_HTML, FilterType.SUBJECT));

        // Getting emails
        emailManager.receive(email);

        // Get messages size
        int count = email.getMessages() != null ? email.getMessages().size() : 0;

        // Setting email file location
        email.setHtmlFile("mcaligares/modules/email/template/emailTemplate.html");

        // Send email
        emailManager.send(email);

        // Getting email
        emailManager.receive(email);
        assertThat(email.getMessages(), notNullValue());
        assertThat(email.getMessages().size(), is(count + 1));

    }

    @Test
    public void sendAttachment() throws EmailException {
        //Creating email entity
        Email email = new Email(GMAIL_USER, GMAIL_PASS, GMAIL_USER,
                GMAIL_USER, SUBJECT_ATTACH, "this is a email with attachment");
        assertThat(email, notNullValue());

        // Setting filter to get emails
        email.setFilter(new Filter(SUBJECT_ATTACH, FilterType.SUBJECT));

        // Getting emails
        emailManager.receive(email);

        // Get messages size
        int count = email.getMessages() != null ? email.getMessages().size() : 0;

        // Setting email attach file location
        email.addAttach("test_file.txt", "Test File", "mcaligares/modules/email/attach/attachment.txt");

        // Send email
        emailManager.send(email);

        // Getting email
        emailManager.receive(email);
        assertThat(email.getMessages(), notNullValue());
        assertThat(email.getMessages().size(), is(count + 1));

    }

}
