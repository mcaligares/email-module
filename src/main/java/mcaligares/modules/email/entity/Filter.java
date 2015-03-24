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

import java.util.Date;

import javax.mail.Message.RecipientType;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.MessageNumberTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SubjectTerm;

import mcaligares.modules.email.exception.EmailException;
import mcaligares.modules.email.exception.EmailFilterException;
import mcaligares.modules.email.utils.EmailUtils;

/**
 * 
 * @author miguel
 *
 */
public final class Filter {

    private Integer num;
    private String term;
    private Date iniDate;
    private Date endDate;
    private FilterType type;
    private FilterDateType dateType;

    public static enum FilterDateType {
        AFTER, BEFORE, BETWEEN
    }

    public static enum FilterType {
        FROM, TO, SUBJECT, BODY, NUMBER, SENT_DATE, RECEIVED_DATE
    }

    public Filter(Integer num) {
        this(num, null, null, null, FilterType.NUMBER, null);
    }

    public Filter(String term, FilterType type) {
        this(null, term, null, null, type, null);
    }

    public Filter(Date iniDate, FilterType type, FilterDateType dateType) {
        this(null, null, iniDate, null, type, dateType);
    }

    public Filter(Date iniDate, Date endDate, FilterType type) {
        this(null, null, iniDate, endDate, type, FilterDateType.BETWEEN);
    }

    public Filter(Integer num, String term, Date iniDate, Date endDate, FilterType type, FilterDateType dateType) {
        super();
        this.num = num;
        this.term = term;
        this.iniDate = iniDate;
        this.endDate = endDate;
        this.type = type;
        this.dateType = dateType;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Date getIniDate() {
        return iniDate;
    }

    public void setIniDate(Date iniDate) {
        this.iniDate = iniDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public FilterDateType getDateType() {
        return dateType;
    }

    public void setDateType(FilterDateType dateType) {
        this.dateType = dateType;
    }

    public SearchTerm getSearchTerm() throws EmailException {
        // Checking for filter type
        if (type == null) throw new EmailFilterException("filter type cannot be null");

        // Switch filter type
        if (type.equals(FilterType.FROM)) {

            // Checking that term is not blank
            if (EmailUtils.isBlank(term))
                throw new EmailFilterException("filter term to FilterType.FROM cannot be null");

            // return FromTerm
            return new FromStringTerm(term);

        } else if (type.equals(FilterType.TO)) {

            // Checking that term is not blank
            if (EmailUtils.isBlank(term))
                throw new EmailFilterException("filter term to FilterType.TO cannot be null");

            // return RecipientTerm
            return new RecipientStringTerm(RecipientType.TO, term);

        } else if (type.equals(FilterType.SUBJECT)) {

            // Checking that term is not blank
            if (EmailUtils.isBlank(term))
                throw new EmailFilterException("filter term to FilterType.SUBJECT cannot be null");

            // return SubjectTerm
            return new SubjectTerm(term);

        } else if (type.equals(FilterType.BODY)) {

            // Checking that term is not blank
            if (EmailUtils.isBlank(term))
                throw new EmailFilterException("filter term to FilterType.BODY cannot be null");

            // return BodyTerm
            return new BodyTerm(term);

        } else if (type.equals(FilterType.NUMBER)) {

            // Checking that num is not null
            if (num == null)
                throw new EmailFilterException("filter num to FilterType.NUMBER cannot be null");

            // return MessageNumberTerm
            return new MessageNumberTerm(num);

        } else if (type.equals(FilterType.SENT_DATE)) {

            // Check that date type is not null
            if (dateType == null)
                throw new EmailFilterException("filter date type to FilterType.SENT_DATE cannot be null");
            // Check that iniDate is not null
            if (iniDate == null)
                throw new EmailFilterException("filter date (iniDate) to FilterType.SENT_DATE cannot be null");

            // Switch date type and return SentDateTerm
            if (dateType.equals(FilterDateType.AFTER)) {
                return new SentDateTerm(ComparisonTerm.GE, iniDate);
            } else if (dateType.equals(FilterDateType.BEFORE)) {
                return new SentDateTerm(ComparisonTerm.LE, iniDate);
            } else if (dateType.equals(FilterDateType.BETWEEN)) {
                // Create a between date filter
                SentDateTerm olderThan = new SentDateTerm(ComparisonTerm.LE, endDate);
                SentDateTerm newerThan = new SentDateTerm(ComparisonTerm.GE, iniDate);
                return new AndTerm(olderThan, newerThan);
            } else {
                // Filter date type not found
                throw new EmailFilterException("filter date type not found!");
            }

        } else if (type.equals(FilterType.RECEIVED_DATE)) {
            // Check that date type is not null
            if (dateType == null)
                throw new EmailFilterException("filter date type to FilterType.RECEIVED_DATE cannot be null");
            // Check that iniDate is not null
            if (iniDate == null)
                throw new EmailFilterException("filter date (iniDate) to FilterType.RECEIVED_DATE cannot be null");

            // Switch date type and return ReceivedDateTerm
            if (dateType.equals(FilterDateType.AFTER)) {
                return new ReceivedDateTerm(ComparisonTerm.GE, iniDate);
            } else if (dateType.equals(FilterDateType.BEFORE)) {
                return new ReceivedDateTerm(ComparisonTerm.LE, iniDate);
            } else if (dateType.equals(FilterDateType.BETWEEN)) {
                // Create a between date filter
                ReceivedDateTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LE, endDate);
                ReceivedDateTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GE, iniDate);
                return new AndTerm(olderThan, newerThan);
            } else {
                // Filter date type not found
                throw new EmailFilterException("filter date type not found!");
            }
        } else {
            // Filter type not found
            throw new EmailFilterException("filter type not found!");
        }
    }

}
