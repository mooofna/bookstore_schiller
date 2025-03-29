package schiller.inventory.productSpecialization.productExtension;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductExtension is a Querydsl query type for ProductExtension
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductExtension extends EntityPathBase<ProductExtension> {

    private static final long serialVersionUID = -1935206856L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductExtension productExtension = new QProductExtension("productExtension");

    public final schiller.inventory.productSpecialization.QProductSpecialization _super;

    //inherited
    public final SetPath<String, StringPath> categories;

    public final schiller.inventory.productSpecialization.productExtension.category.QCategory category;

    //inherited
    public final ListPath<schiller.inventory.productSpecialization.comment.Comment, schiller.inventory.productSpecialization.comment.QComment> comments;

    // inherited
    public final org.salespointframework.catalog.QProduct_ProductIdentifier id;

    public final StringPath image = createString("image");

    public final StringPath info = createString("info");

    //inherited
    public final EnumPath<org.salespointframework.quantity.Metric> metric;

    //inherited
    public final StringPath name;

    //inherited
    public final SimplePath<javax.money.MonetaryAmount> price;

    public QProductExtension(String variable) {
        this(ProductExtension.class, forVariable(variable), INITS);
    }

    public QProductExtension(Path<? extends ProductExtension> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductExtension(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductExtension(PathMetadata metadata, PathInits inits) {
        this(ProductExtension.class, metadata, inits);
    }

    public QProductExtension(Class<? extends ProductExtension> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new schiller.inventory.productSpecialization.QProductSpecialization(type, metadata, inits);
        this.categories = _super.categories;
        this.category = inits.isInitialized("category") ? new schiller.inventory.productSpecialization.productExtension.category.QCategory(forProperty("category")) : null;
        this.comments = _super.comments;
        this.id = _super.id;
        this.metric = _super.metric;
        this.name = _super.name;
        this.price = _super.price;
    }

}

