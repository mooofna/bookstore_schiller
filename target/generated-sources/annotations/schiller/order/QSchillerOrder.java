package schiller.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSchillerOrder is a Querydsl query type for SchillerOrder
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchillerOrder extends EntityPathBase<SchillerOrder> {

    private static final long serialVersionUID = 254849344L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSchillerOrder schillerOrder = new QSchillerOrder("schillerOrder");

    public final org.salespointframework.order.QOrder _super;

    //inherited
    public final ListPath<org.salespointframework.order.ChargeLine.AttachedChargeLine, org.salespointframework.order.QChargeLine_AttachedChargeLine> attachedChargeLines;

    //inherited
    public final ListPath<org.salespointframework.order.ChargeLine, org.salespointframework.order.QChargeLine> chargeLines;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateCreated;

    public final QDeliveryMethod deliveryMethod;

    // inherited
    public final org.salespointframework.order.QOrder_OrderIdentifier orderIdentifier;

    //inherited
    public final ListPath<org.salespointframework.order.OrderLine, org.salespointframework.order.QOrderLine> orderLines;

    //inherited
    public final EnumPath<org.salespointframework.order.OrderStatus> orderStatus;

    //inherited
    public final SimplePath<org.salespointframework.payment.PaymentMethod> paymentMethod;

    public final EnumPath<org.salespointframework.order.OrderStatus> status = createEnum("status", org.salespointframework.order.OrderStatus.class);

    // inherited
    public final org.salespointframework.useraccount.QUserAccount_UserAccountIdentifier userAccountIdentifier;

    public final StringPath userName = createString("userName");

    public QSchillerOrder(String variable) {
        this(SchillerOrder.class, forVariable(variable), INITS);
    }

    public QSchillerOrder(Path<? extends SchillerOrder> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSchillerOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSchillerOrder(PathMetadata metadata, PathInits inits) {
        this(SchillerOrder.class, metadata, inits);
    }

    public QSchillerOrder(Class<? extends SchillerOrder> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.order.QOrder(type, metadata, inits);
        this.attachedChargeLines = _super.attachedChargeLines;
        this.chargeLines = _super.chargeLines;
        this.dateCreated = _super.dateCreated;
        this.deliveryMethod = inits.isInitialized("deliveryMethod") ? new QDeliveryMethod(forProperty("deliveryMethod")) : null;
        this.orderIdentifier = _super.orderIdentifier;
        this.orderLines = _super.orderLines;
        this.orderStatus = _super.orderStatus;
        this.paymentMethod = _super.paymentMethod;
        this.userAccountIdentifier = _super.userAccountIdentifier;
    }

}

