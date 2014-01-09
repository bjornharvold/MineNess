package com.mineness.domain.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
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
@Table(name = "JAMES_MAIL_USERFLAG")
@Configurable
public class JamesMailUserflag implements Serializable {

    private static final long serialVersionUID = 6065651871747410627L;

    @PersistenceContext
    transient EntityManager entityManager;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USERFLAG_ID")
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new JamesMailUserflag().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countJamesMailUserflags() {
        return entityManager().createQuery("SELECT COUNT(o) FROM JamesMailUserflag o", Long.class).getSingleResult();
    }

    public static List<JamesMailUserflag> findAllJamesMailUserflags() {
        return entityManager().createQuery("SELECT o FROM JamesMailUserflag o", JamesMailUserflag.class).getResultList();
    }

    public static JamesMailUserflag findJamesMailUserflag(Long id) {
        if (id == null) return null;
        return entityManager().find(JamesMailUserflag.class, id);
    }

    public static List<JamesMailUserflag> findJamesMailUserflagEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM JamesMailUserflag o", JamesMailUserflag.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            JamesMailUserflag attached = findJamesMailUserflag(this.id);
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
    public JamesMailUserflag merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        JamesMailUserflag merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
