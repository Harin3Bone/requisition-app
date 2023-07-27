CREATE TABLE public.item
(
    itemid                uuid                     DEFAULT uuid_generate_v4() NOT NULL
        CONSTRAINT item_pk
            PRIMARY KEY,
    categoryid            uuid,
    quantity              INTEGER                  DEFAULT 0,
    lastoperatorid        uuid,
    created_timestamp     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    lastupdated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP  NOT NULL
);

CREATE INDEX item_index
    ON public.item (itemid, categoryid);

CREATE TABLE public.category
(
    categoryid            uuid                     DEFAULT uuid_generate_v4() NOT NULL
        CONSTRAINT category_pk
            PRIMARY KEY,
    name                  VARCHAR(250)             DEFAULT '':: CHARACTER VARYING NOT NULL,
    lastoperatorid        uuid,
    created_timestamp     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    lastupdated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP  NOT NULL
);

CREATE TABLE public.student
(
    studentid             uuid        DEFAULT uuid_generate_v4() NOT NULL
        CONSTRAINT student_pk
            PRIMARY KEY,
    username              VARCHAR(20) DEFAULT '':: CHARACTER VARYING NOT NULL
        CONSTRAINT student_username_uk
        UNIQUE,
    password              VARCHAR,
    email                 VARCHAR(250),
    teacherid             uuid,
    status                VARCHAR(30),
    lastoperatorid        uuid,
    created_timestamp     TIMESTAMP WITH TIME ZONE               NOT NULL,
    lastupdated_timestamp TIMESTAMP WITH TIME ZONE               NOT NULL
);

CREATE TABLE public.teacher
(
    teacherid             uuid        DEFAULT uuid_generate_v4() NOT NULL
        CONSTRAINT teacher_pk
            PRIMARY KEY,
    username              VARCHAR(20) DEFAULT '':: CHARACTER VARYING NOT NULL
        CONSTRAINT teacher_username_uk
        UNIQUE,
    password              VARCHAR,
    email                 VARCHAR(250),
    status                VARCHAR(30),
    lastoperatorid        uuid,
    created_timestamp     TIMESTAMP WITH TIME ZONE               NOT NULL,
    lastupdated_timestamp TIMESTAMP WITH TIME ZONE               NOT NULL
);

CREATE TABLE public.admin
(
    adminid               uuid        DEFAULT uuid_generate_v4() NOT NULL
        CONSTRAINT admin_pk
            PRIMARY KEY,
    username              VARCHAR(20) DEFAULT '':: CHARACTER VARYING NOT NULL
        CONSTRAINT admin_username_uk
        UNIQUE,
    password              VARCHAR,
    email                 VARCHAR(250),
    status                VARCHAR(30),
    lastoperatorid        uuid,
    created_timestamp     TIMESTAMP WITH TIME ZONE               NOT NULL,
    lastupdated_timestamp TIMESTAMP WITH TIME ZONE               NOT NULL
);

CREATE TABLE public.requisition
(
    requisitionid              uuid                     DEFAULT uuid_generate_v4() NOT NULL
        CONSTRAINT requisition_pk
            PRIMARY KEY,
    subject                    VARCHAR(100),
    description                VARCHAR(300)             DEFAULT '':: CHARACTER VARYING,
    status                     VARCHAR(30),
    requesterid                uuid,
    teacherid                  uuid,
    adminid                    uuid,
    teacher_approval_timestamp TIMESTAMP WITH TIME ZONE,
    admin_approval_timestamp   TIMESTAMP WITH TIME ZONE,
    lastoperatorid             uuid,
    created_timestamp          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    lastupdated_timestamp      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP  NOT NULL
);

CREATE TABLE public.requisition_item
(
    requisition_itemid    uuid                     DEFAULT uuid_generate_v4() NOT NULL
        CONSTRAINT requisition_item_pk
            PRIMARY KEY,
    requisitionid         uuid                                                NOT NULL,
    itemid                uuid                                                NOT NULL,
    quantity              INTEGER                  DEFAULT 0                  NOT NULL,
    lastoperatorid        uuid,
    created_timestamp     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    lastupdated_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP  NOT NULL
);

CREATE INDEX requisition_item_index
    ON public.requisition_item (requisition_itemid, requisitionid, itemid);

