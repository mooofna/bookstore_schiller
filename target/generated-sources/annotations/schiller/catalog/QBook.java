package schiller.catalog;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBook is a Querydsl query type for Book
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBook extends EntityPathBase<Book> {

    private static final long serialVersionUID = 926578350L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBook book = new QBook("book");

    public final org.salespointframework.catalog.QProduct _super;

    public final QAuthor author;

    //inherited
    public final SetPath<String, StringPath> categories;

    public final ListPath<Comment, QComment> comments = this.<Comment, QComment>createList("comments", Comment.class, QComment.class, PathInits.DIRECT2);

    public final StringPath coverText = createString("coverText");

    public final EnumPath<Book.GENRE> genre = createEnum("genre", Book.GENRE.class);

    // inherited
    public final org.salespointframework.catalog.QProduct_ProductIdentifier id;

    public final StringPath image = createString("image");

    public final StringPath isbn = createString("isbn");

    //inherited
    public final EnumPath<org.salespointframework.quantity.Metric> metric;

    //inherited
    public final StringPath name;

    //inherited
    public final SimplePath<javax.money.MonetaryAmount> price;

    public final StringPath publisher = createString("publisher");

    public QBook(String variable) {
        this(Book.class, forVariable(variable), INITS);
    }

    public QBook(Path<? extends Book> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBook(PathMetadata metadata, PathInits inits) {
        this(Book.class, metadata, inits);
    }

    public QBook(Class<? extends Book> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.catalog.QProduct(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QAuthor(forProperty("author")) : null;
        this.categories = _super.categories;
        this.id = _super.id;
        this.metric = _super.metric;
        this.name = _super.name;
        this.price = _super.price;
    }

}

