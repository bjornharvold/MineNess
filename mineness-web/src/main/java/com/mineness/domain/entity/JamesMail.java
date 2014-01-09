package com.mineness.domain.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Lob;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "JAMES_MAIL")
@Configurable
public class JamesMail implements Serializable {

    private static final long serialVersionUID = 5895978907409503755L;

    @PersistenceContext
    transient EntityManager entityManager;

    @EmbeddedId
    private JamesMailPK id;

    @Column(name = "MAIL_IS_ANSWERED", nullable = false, columnDefinition = "BIT")
    private Boolean mailIsAnswered;

    @Column(name = "MAIL_BODY_START_OCTET", nullable = false)
    private Integer mailBodyStartOctet;

    @Column(name = "MAIL_CONTENT_OCTETS_COUNT", nullable = false)
    private Long mailContentOctetsCount;

    @Column(name = "MAIL_IS_DELETED", nullable = false, columnDefinition = "BIT")
    private Boolean mailIsDeleted;

    @Column(name = "MAIL_IS_DRAFT", nullable = false, columnDefinition = "BIT")
    private Boolean mailIsDraft;

    @Column(name = "MAIL_IS_FLAGGED", nullable = false, columnDefinition = "BIT")
    private Boolean mailIsFlagged;

    @Column(name = "MAIL_IS_RECENT", nullable = false, columnDefinition = "BIT")
    private Boolean mailIsRecent;

    @Column(name = "MAIL_IS_SEEN", nullable = false, columnDefinition = "BIT")
    private Boolean mailIsSeen;

    @Column(name = "MAIL_DATE", nullable = true)
    private Date mailDate;

    @Column(name = "MAIL_MIME_TYPE", nullable = true)
    private String mailMimeType;

    @Column(name = "MAIL_MIME_SUBTYPE", nullable = true)
    private String mailMimeSubType;

    @Column(name = "MAIL_MODSEQ", nullable = true)
    private Long mailModSeq;

    @Column(name = "MAIL_TEXTUAL_LINE_COUNT", nullable = true)
    private Long mailTextualLineCount;

    @Lob
    @Column(name = "MAIL_BYTES", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] mailBytes;

    @Lob
    @Column(name = "HEADER_BYTES", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] headerBytes;

    public JamesMailPK getId() {
        return this.id;
    }

    public void setId(JamesMailPK id) {
        this.id = id;
    }

    public Boolean getMailIsAnswered() {
        return mailIsAnswered;
    }

    public void setMailIsAnswered(Boolean mailIsAnswered) {
        this.mailIsAnswered = mailIsAnswered;
    }

    public Integer getMailBodyStartOctet() {
        return mailBodyStartOctet;
    }

    public void setMailBodyStartOctet(Integer mailBodyStartOctet) {
        this.mailBodyStartOctet = mailBodyStartOctet;
    }

    public Long getMailContentOctetsCount() {
        return mailContentOctetsCount;
    }

    public void setMailContentOctetsCount(Long mailContentOctetsCount) {
        this.mailContentOctetsCount = mailContentOctetsCount;
    }

    public Boolean getMailIsDeleted() {
        return mailIsDeleted;
    }

    public void setMailIsDeleted(Boolean mailIsDeleted) {
        this.mailIsDeleted = mailIsDeleted;
    }

    public Boolean getMailIsDraft() {
        return mailIsDraft;
    }

    public void setMailIsDraft(Boolean mailIsDraft) {
        this.mailIsDraft = mailIsDraft;
    }

    public Boolean getMailIsFlagged() {
        return mailIsFlagged;
    }

    public void setMailIsFlagged(Boolean mailIsFlagged) {
        this.mailIsFlagged = mailIsFlagged;
    }

    public Boolean getMailIsRecent() {
        return mailIsRecent;
    }

    public void setMailIsRecent(Boolean mailIsRecent) {
        this.mailIsRecent = mailIsRecent;
    }

    public Boolean getMailIsSeen() {
        return mailIsSeen;
    }

    public void setMailIsSeen(Boolean mailIsSeen) {
        this.mailIsSeen = mailIsSeen;
    }

    public Date getMailDate() {
        return mailDate;
    }

    public void setMailDate(Date mailDate) {
        this.mailDate = mailDate;
    }

    public String getMailMimeType() {
        return mailMimeType;
    }

    public void setMailMimeType(String mailMimeType) {
        this.mailMimeType = mailMimeType;
    }

    public String getMailMimeSubType() {
        return mailMimeSubType;
    }

    public void setMailMimeSubType(String mailMimeSubType) {
        this.mailMimeSubType = mailMimeSubType;
    }

    public Long getMailModSeq() {
        return mailModSeq;
    }

    public void setMailModSeq(Long mailModSeq) {
        this.mailModSeq = mailModSeq;
    }

    public Long getMailTextualLineCount() {
        return mailTextualLineCount;
    }

    public void setMailTextualLineCount(Long mailTextualLineCount) {
        this.mailTextualLineCount = mailTextualLineCount;
    }

    public byte[] getMailBytes() {
        return mailBytes;
    }

    public void setMailBytes(byte[] mailBytes) {
        this.mailBytes = mailBytes;
    }

    public byte[] getHeaderBytes() {
        return headerBytes;
    }

    public void setHeaderBytes(byte[] headerBytes) {
        this.headerBytes = headerBytes;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new JamesMail().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countJamesMails() {
        return entityManager().createQuery("SELECT COUNT(o) FROM JamesMail o", Long.class).getSingleResult();
    }

    public static List<JamesMail> findAllJamesMails() {
        return entityManager().createQuery("SELECT o FROM JamesMail o", JamesMail.class).getResultList();
    }

    public static JamesMail findJamesMail(JamesMailPK id) {
        if (id == null) return null;
        return entityManager().find(JamesMail.class, id);
    }

    public static List<JamesMail> findJamesMailEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM JamesMail o", JamesMail.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    @Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            JamesMail attached = findJamesMail(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

    @Transactional
    public JamesMail merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        JamesMail merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
