package schiller.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDelivery is a Querydsl query type for Delivery
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDelivery extends EntityPathBase<Delivery> {

    private static final long serialVersionUID = 493424036L;

    public static final QDelivery delivery = new QDelivery("delivery");

    public final QDeliveryMethod _super = new QDeliveryMethod(this);

    public final EnumPath<Delivery.DeliveryStatus> deliveryStatus = createEnum("deliveryStatus", Delivery.DeliveryStatus.class);

    //inherited
    public final StringPath description = _super.description;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath shippingAddress = createString("shippingAddress");

    public QDelivery(String variable) {
        super(Delivery.class, forVariable(variable));
    }

    public QDelivery(Path<? extends Delivery> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDelivery(PathMetadata metadata) {
        super(Delivery.class, metadata);
    }

}

