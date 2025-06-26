package com.example.bootJPA.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFile is a Querydsl query type for File
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFile extends EntityPathBase<File> {

    private static final long serialVersionUID = -661109647L;

    public static final QFile file = new QFile("file");

    public final QTimeBase _super = new QTimeBase(this);

    public final NumberPath<Long> bno = createNumber("bno", Long.class);

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final NumberPath<Integer> fileType = createNumber("fileType", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath saveDir = createString("saveDir");

    public final StringPath uuid = createString("uuid");

    public QFile(String variable) {
        super(File.class, forVariable(variable));
    }

    public QFile(Path<? extends File> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFile(PathMetadata metadata) {
        super(File.class, metadata);
    }

}

