package schiller.support;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIssue is a Querydsl query type for Issue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIssue extends EntityPathBase<Issue> {

    private static final long serialVersionUID = -748231446L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIssue issue = new QIssue("issue");

    public final BooleanPath closed = createBoolean("closed");

    public final StringPath content = createString("content");

    public final schiller.user.QCustomer customer;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Message, QMessage> messages = this.<Message, QMessage>createList("messages", Message.class, QMessage.class, PathInits.DIRECT2);

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    public final StringPath title = createString("title");

    public QIssue(String variable) {
        this(Issue.class, forVariable(variable), INITS);
    }

    public QIssue(Path<? extends Issue> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIssue(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIssue(PathMetadata metadata, PathInits inits) {
        this(Issue.class, metadata, inits);
    }

    public QIssue(Class<? extends Issue> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new schiller.user.QCustomer(forProperty("customer"), inits.get("customer")) : null;
    }

}

