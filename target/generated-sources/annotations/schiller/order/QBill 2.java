package schiller.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBill is a Querydsl query type for Bill
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBill extends EntityPathBase<Bill> {

    private static final long serialVersionUID = 1444834743L;

    public static final QBill bill = new QBill("bill");

    public final StringPath billingAddress = createString("billingAddress");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath sent = createBoolean("sent");

    public QBill(String variable) {
        super(Bill.class, forVariable(variable));
    }

    public QBill(Path<? extends Bill> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBill(PathMetadata metadata) {
        super(Bill.class, metadata);
    }

}

