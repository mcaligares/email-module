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

import java.io.IOException;
import java.net.URL;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import mcaligares.modules.email.exception.EmailException;
import mcaligares.modules.email.utils.EmailUtils;

/**
 * 
 * @author miguel
 *
 */
public class Attachment {

    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    private URL url;
    private String path;
    private String name;
    private String description;
    private String disposition;

    public Attachment() {
        super();
    }

    public Attachment(String name, String path) {
        this(name, null, null, path, null);
    }

    public Attachment(String name, String description, String path) {
        this(name, description, null, path, null);
    }

    public Attachment(String name, String description, String disposition, String path) {
        this(name, description, disposition, path, null);
    }

    public Attachment(String name, String description, String disposition, String path, URL url) {
        super();
        this.setName(name);
        this.setDescription(description);
        this.setDisposition(disposition);
        this.setPath(path);
        this.setUrl(url);
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public DataSource getDataSource() throws IOException, EmailException {
        return new ByteArrayDataSource(EmailUtils.readFileAsString(this.getPath()), APPLICATION_OCTET_STREAM);
    }

}
