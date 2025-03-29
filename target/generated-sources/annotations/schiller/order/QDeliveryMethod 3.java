package schiller.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeliveryMethod is a Querydsl query type for DeliveryMethod
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeliveryMethod extends EntityPathBase<DeliveryMethod> {

    private static final long serialVersionUID = -934019163L;

    public static final QDeliveryMethod deliveryMethod = new QDeliveryMethod("deliveryMethod");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QDeliveryMethod(String variable) {
        super(DeliveryMethod.class, forVariable(variable));
    }

    public QDeliveryMethod(Path<? extends DeliveryMethod> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeliveryMethod(PathMetadata metadata) {
        super(DeliveryMethod.class, metadata);
    }

}

