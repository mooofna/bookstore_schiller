package schiller.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPickup is a Querydsl query type for Pickup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPickup extends EntityPathBase<Pickup> {

    private static final long serialVersionUID = 1612294188L;

    public static final QPickup pickup = new QPickup("pickup");

    public final QDeliveryMethod _super = new QDeliveryMethod(this);

    //inherited
    public final StringPath description = _super.description;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath pickedUp = createBoolean("pickedUp");

    public QPickup(String variable) {
        super(Pickup.class, forVariable(variable));
    }

    public QPickup(Path<? extends Pickup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPickup(PathMetadata metadata) {
        super(Pickup.class, metadata);
    }

}

