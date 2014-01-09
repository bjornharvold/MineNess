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
@Table(name = "OPENJPA_SEQUENCE_TABLE")
@Configurable
public class OpenjpaSequenceTable implements Serializable {
    private static final long serialVersionUID = -3769323112550757741L;

    @PersistenceContext
    transient EntityManager entityManager;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", columnDefinition = "TINYINT")
    private Integer id;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static final EntityManager entityManager() {
        EntityManager em = new OpenjpaSequenceTable().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countOpenjpaSequenceTables() {
        return entityManager().createQuery("SELECT COUNT(o) FROM OpenjpaSequenceTable o", Long.class).getSingleResult();
    }

    public static List<OpenjpaSequenceTable> findAllOpenjpaSequenceTables() {
        return entityManager().createQuery("SELECT o FROM OpenjpaSequenceTable o", OpenjpaSequenceTable.class).getResultList();
    }

    public static OpenjpaSequenceTable findOpenjpaSequenceTable(Integer id) {
        if (id == null) return null;
        return entityManager().find(OpenjpaSequenceTable.class, id);
    }

    public static List<OpenjpaSequenceTable> findOpenjpaSequenceTableEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM OpenjpaSequenceTable o", OpenjpaSequenceTable.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            OpenjpaSequenceTable attached = findOpenjpaSequenceTable(this.id);
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
    public OpenjpaSequenceTable merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        OpenjpaSequenceTable merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
