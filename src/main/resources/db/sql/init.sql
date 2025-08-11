use to_do_list;

CREATE TABLE IF NOT EXISTS tb_user(
    id varchar(36) PRIMARY KEY,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL UNIQUE,
    password varchar(200) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tb_task(
    id varchar(36) PRIMARY KEY,
    title varchar(50) NOT NULL,
    description varchar(200) NOT NULL,
    due_date date not null,
    status enum('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA') NOT NULL,
    priority enum('ALTA', 'BAIXA', 'REGULAR') NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id varchar(36) NOT NULL,
    parent_id varchar(36),
    CONSTRAINT fk_task_user FOREIGN KEY(user_id)
        REFERENCES tb_user(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_task_parent FOREIGN KEY(parent_id)
        REFERENCES tb_task(id)
        ON DELETE CASCADE
);

