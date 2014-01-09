package com.mineness.domain.entity;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configurable
@Embeddable
public final class JamesRecipientRewritePK implements Serializable {

    private static final long serialVersionUID = 2150249079351821521L;

    @Column(name = "DOMAIN_NAME", nullable = false)
    private String domainName;

    @Column(name = "USER_NAME", nullable = false)
    private String username;

    public JamesRecipientRewritePK(String domainName, String username) {
        this.domainName = domainName;
        this.username = username;
    }

    public JamesRecipientRewritePK() {
    }

    public String getDomainName() {
        return domainName;
    }

    public String getUsername() {
        return username;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public static JamesRecipientRewritePK fromJsonToJamesRecipientRewritePK(String json) {
        return new JSONDeserializer<JamesRecipientRewritePK>().use(null, JamesRecipientRewritePK.class).deserialize(json);
    }

    public static String toJsonArray(Collection<JamesRecipientRewritePK> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<JamesRecipientRewritePK> fromJsonArrayToJamesRecipientRewritePKs(String json) {
        return new JSONDeserializer<List<JamesRecipientRewritePK>>().use(null, ArrayList.class).use("values", JamesRecipientRewritePK.class).deserialize(json);
    }
}
