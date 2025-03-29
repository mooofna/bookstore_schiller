package schiller.inventory.productSpecialization;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductSpecialization is a Querydsl query type for ProductSpecialization
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductSpecialization extends EntityPathBase<ProductSpecialization> {

    private static final long serialVersionUID = -1143707868L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductSpecialization productSpecialization = new QProductSpecialization("productSpecialization");

    public final org.salespointframework.catalog.QProduct _super;

    //inherited
    public final SetPath<String, StringPath> categories;

    public final ListPath<schiller.inventory.productSpecialization.comment.Comment, schiller.inventory.productSpecialization.comment.QComment> comments = this.<schiller.inventory.productSpecialization.comment.Comment, schiller.inventory.productSpecialization.comment.QComment>createList("comments", schiller.inventory.productSpecialization.comment.Comment.class, schiller.inventory.productSpecialization.comment.QComment.class, PathInits.DIRECT2);

    // inherited
    public final org.salespointframework.catalog.QProduct_ProductIdentifier id;

    //inherited
    public final EnumPath<org.salespointframework.quantity.Metric> metric;

    //inherited
    public final StringPath name;

    //inherited
    public final SimplePath<javax.money.MonetaryAmount> price;

    public QProductSpecialization(String variable) {
        this(ProductSpecialization.class, forVariable(variable), INITS);
    }

    public QProductSpecialization(Path<? extends ProductSpecialization> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductSpecialization(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductSpecialization(PathMetadata metadata, PathInits inits) {
        this(ProductSpecialization.class, metadata, inits);
    }

    public QProductSpecialization(Class<? extends ProductSpecialization> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.catalog.QProduct(type, metadata, inits);
        this.categories = _super.categories;
        this.id = _super.id;
        this.metric = _super.metric;
        this.name = _super.name;
        this.price = _super.price;
    }

}

