package schiller.catalog.book;

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

    private static final long serialVersionUID = 1291865705L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBook book = new QBook("book");

    public final org.salespointframework.catalog.QProduct _super;

    public final schiller.catalog.author.QAuthor author;

    //inherited
    public final SetPath<String, StringPath> categories;

    public final ListPath<schiller.catalog.comment.Comment, schiller.catalog.comment.QComment> comments = this.<schiller.catalog.comment.Comment, schiller.catalog.comment.QComment>createList("comments", schiller.catalog.comment.Comment.class, schiller.catalog.comment.QComment.class, PathInits.DIRECT2);

    public final StringPath coverText = createString("coverText");

    public final SetPath<schiller.catalog.genre.Genre, schiller.catalog.genre.QGenre> genres = this.<schiller.catalog.genre.Genre, schiller.catalog.genre.QGenre>createSet("genres", schiller.catalog.genre.Genre.class, schiller.catalog.genre.QGenre.class, PathInits.DIRECT2);

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
        this.author = inits.isInitialized("author") ? new schiller.catalog.author.QAuthor(forProperty("author")) : null;
        this.categories = _super.categories;
        this.id = _super.id;
        this.metric = _super.metric;
        this.name = _super.name;
        this.price = _super.price;
    }

}

