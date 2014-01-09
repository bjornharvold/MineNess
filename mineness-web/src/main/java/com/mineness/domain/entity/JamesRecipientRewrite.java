package com.mineness.domain.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "JAMES_RECIPIENT_REWRITE")
@Configurable
public class JamesRecipientRewrite implements Serializable {
    private static final long serialVersionUID = 7420156195552116394L;

    @PersistenceContext
    transient EntityManager entityManager;

    @EmbeddedId
    private JamesRecipientRewritePK id;

    public JamesRecipientRewritePK getId() {
        return this.id;
    }

    public void setId(JamesRecipientRewritePK id) {
        this.id = id;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new JamesRecipientRewrite().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countJamesRecipientRewrites() {
        return entityManager().createQuery("SELECT COUNT(o) FROM JamesRecipientRewrite o", Long.class).getSingleResult();
    }

    public static List<JamesRecipientRewrite> findAllJamesRecipientRewrites() {
        return entityManager().createQuery("SELECT o FROM JamesRecipientRewrite o", JamesRecipientRewrite.class).getResultList();
    }

    public static JamesRecipientRewrite findJamesRecipientRewrite(JamesRecipientRewritePK id) {
        if (id == null) return null;
        return entityManager().find(JamesRecipientRewrite.class, id);
    }

    public static List<JamesRecipientRewrite> findJamesRecipientRewriteEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM JamesRecipientRewrite o", JamesRecipientRewrite.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            JamesRecipientRewrite attached = findJamesRecipientRewrite(this.id);
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
    public JamesRecipientRewrite merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        JamesRecipientRewrite merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
