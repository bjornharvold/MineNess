package com.mineness.domain.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "JAMES_MAILBOX")
@Configurable
public class JamesMailbox implements Serializable {

    private static final long serialVersionUID = -7144256576403956046L;

    @PersistenceContext
    transient EntityManager entityManager;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MAILBOX_ID")
    private Long mailboxId;

    @Column(name = "MAILBOX_HIGHEST_MODSEQ", nullable = false)
    private Long mailboxHighestModSeq;

    @Column(name = "MAILBOX_LAST_UID", nullable = false)
    private Long mailboxLastUid;

    @Column(name = "MAILBOX_UID_VALIDITY", nullable = false)
    private Long mailboxUidValidity;

    @Column(name = "MAILBOX_NAME", nullable = false)
    private String mailboxName;

    @Column(name = "MAILBOX_NAMESPACE", nullable = false)
    private String mailboxNamespace;

    @Column(name = "USER_NAME", nullable = false)
    private String username;

    public Long getMailboxId() {
        return this.mailboxId;
    }

    public void setMailboxId(Long mailboxId) {
        this.mailboxId = mailboxId;
    }

    public Long getMailboxHighestModSeq() {
        return mailboxHighestModSeq;
    }

    public void setMailboxHighestModSeq(Long mailboxHighestModSeq) {
        this.mailboxHighestModSeq = mailboxHighestModSeq;
    }

    public Long getMailboxLastUid() {
        return mailboxLastUid;
    }

    public void setMailboxLastUid(Long mailboxLastUid) {
        this.mailboxLastUid = mailboxLastUid;
    }

    public Long getMailboxUidValidity() {
        return mailboxUidValidity;
    }

    public void setMailboxUidValidity(Long mailboxUidValidity) {
        this.mailboxUidValidity = mailboxUidValidity;
    }

    public String getMailboxName() {
        return mailboxName;
    }

    public void setMailboxName(String mailboxName) {
        this.mailboxName = mailboxName;
    }

    public String getMailboxNamespace() {
        return mailboxNamespace;
    }

    public void setMailboxNamespace(String mailboxNamespace) {
        this.mailboxNamespace = mailboxNamespace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new JamesMailbox().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countJamesMailboxes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM JamesMailbox o", Long.class).getSingleResult();
    }

    public static List<JamesMailbox> findAllJamesMailboxes() {
        return entityManager().createQuery("SELECT o FROM JamesMailbox o", JamesMailbox.class).getResultList();
    }

    public static JamesMailbox findJamesMailbox(Long id) {
        if (id == null) return null;
        return entityManager().find(JamesMailbox.class, id);
    }

    public static List<JamesMailbox> findJamesMailboxEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM JamesMailbox o", JamesMailbox.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            JamesMailbox attached = findJamesMailbox(this.mailboxId);
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
    public JamesMailbox merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        JamesMailbox merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
