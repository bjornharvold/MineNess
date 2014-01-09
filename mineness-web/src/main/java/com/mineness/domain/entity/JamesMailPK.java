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
public final class JamesMailPK implements Serializable {
    private static final long serialVersionUID = 7973981372543853782L;

    @Column(name = "MAILBOX_ID", nullable = false)
    private Long mailboxId;

    @Column(name = "MAIL_UID", nullable = false)
    private Long mailUid;

    public JamesMailPK(Long mailboxId, Long mailUid) {
        this.mailboxId = mailboxId;
        this.mailUid = mailUid;
    }

    public JamesMailPK() {
    }

    public Long getMailboxId() {
        return mailboxId;
    }

    public Long getMailUid() {
        return mailUid;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public static JamesMailPK fromJsonToJamesMailPK(String json) {
        return new JSONDeserializer<JamesMailPK>().use(null, JamesMailPK.class).deserialize(json);
    }

    public static String toJsonArray(Collection<JamesMailPK> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static Collection<JamesMailPK> fromJsonArrayToJamesMailPKs(String json) {
        return new JSONDeserializer<List<JamesMailPK>>().use(null, ArrayList.class).use("values", JamesMailPK.class).deserialize(json);
    }
}
