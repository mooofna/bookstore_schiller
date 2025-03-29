package schiller.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser_UserIdentifier is a Querydsl query type for UserIdentifier
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUser_UserIdentifier extends BeanPath<User.UserIdentifier> {

    private static final long serialVersionUID = -1870250076L;

    public static final QUser_UserIdentifier userIdentifier = new QUser_UserIdentifier("userIdentifier");

    public final ComparablePath<java.util.UUID> identifier = createComparable("identifier", java.util.UUID.class);

    public QUser_UserIdentifier(String variable) {
        super(User.UserIdentifier.class, forVariable(variable));
    }

    public QUser_UserIdentifier(Path<User.UserIdentifier> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser_UserIdentifier(PathMetadata metadata) {
        super(User.UserIdentifier.class, metadata);
    }

}

