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
@Table(name = "JAMES_DOMAIN")
@Configurable
public class JamesDomain implements Serializable {

    private static final long serialVersionUID = 1310635326000465456L;

    @PersistenceContext
    transient EntityManager entityManager;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DOMAIN_NAME")
    private String domainName;

    public String getDomainName() {
        return this.domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new JamesDomain().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countJamesDomains() {
        return entityManager().createQuery("SELECT COUNT(o) FROM JamesDomain o", Long.class).getSingleResult();
    }

    public static List<JamesDomain> findAllJamesDomains() {
        return entityManager().createQuery("SELECT o FROM JamesDomain o", JamesDomain.class).getResultList();
    }

    public static JamesDomain findJamesDomain(String domainName) {
        if (domainName == null) return null;
        return entityManager().find(JamesDomain.class, domainName);
    }

    public static List<JamesDomain> findJamesDomainEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM JamesDomain o", JamesDomain.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            JamesDomain attached = JamesDomain.findJamesDomain(this.domainName);
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
    public JamesDomain merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        JamesDomain merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
