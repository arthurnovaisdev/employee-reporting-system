CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       cpf VARCHAR(11) NOT NULL,
                       contact_email VARCHAR(255),
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(255) NOT NULL,
                       active BOOLEAN NOT NULL,
                       password_changed BOOLEAN NOT NULL,
                       token_version BIGINT NOT NULL,
                       created_at TIMESTAMP(6) NOT NULL,

                       CONSTRAINT uk_users_cpf UNIQUE (cpf),
                       CONSTRAINT chk_users_role
                           CHECK (role IN ('EMPLOYEE', 'ADMIN'))
);

CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            active BOOLEAN NOT NULL,

                            CONSTRAINT uk_categories_name UNIQUE (name)
);

CREATE TABLE reports (
                         id UUID PRIMARY KEY,
                         protocol VARCHAR(255) NOT NULL,
                         access_code_hash VARCHAR(255) NOT NULL,
                         category_id UUID NOT NULL,
                         description TEXT NOT NULL,
                         incident_date DATE,
                         incident_location VARCHAR(255),
                         status VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP(6),
                         updated_at TIMESTAMP(6),

                         CONSTRAINT uk_reports_protocol UNIQUE (protocol),

                         CONSTRAINT fk_reports_category
                             FOREIGN KEY (category_id)
                                 REFERENCES categories(id),

                         CONSTRAINT chk_reports_status
                             CHECK (
                                 status IN (
                                            'RECEIVED',
                                            'IN_ANALYSIS',
                                            'UNDER_INVESTIGATION',
                                            'AWAITING_ACTION',
                                            'CLOSED',
                                            'ARCHIVED'
                                     )
                                 )
);

CREATE TABLE attachments (
                             id UUID PRIMARY KEY,
                             report_id UUID NOT NULL,
                             original_file_name VARCHAR(255) NOT NULL,
                             stored_file_name VARCHAR(255) NOT NULL,
                             content_type VARCHAR(255) NOT NULL,
                             file_size BIGINT,
                             created_at TIMESTAMP(6),

                             CONSTRAINT uk_attachments_stored_file_name
                                 UNIQUE (stored_file_name),

                             CONSTRAINT fk_attachments_report
                                 FOREIGN KEY (report_id)
                                     REFERENCES reports(id)
);

CREATE TABLE status_histories (
                                  id UUID PRIMARY KEY,
                                  report_id UUID NOT NULL,
                                  status VARCHAR(255) NOT NULL,
                                  observation TEXT,
                                  created_at TIMESTAMP(6),

                                  CONSTRAINT fk_status_histories_report
                                      FOREIGN KEY (report_id)
                                          REFERENCES reports(id),

                                  CONSTRAINT chk_status_histories_status
                                      CHECK (
                                          status IN (
                                                     'RECEIVED',
                                                     'IN_ANALYSIS',
                                                     'UNDER_INVESTIGATION',
                                                     'AWAITING_ACTION',
                                                     'CLOSED',
                                                     'ARCHIVED'
                                              )
                                          )
);

CREATE TABLE audit_logs (
                            id UUID PRIMARY KEY,
                            admin_user_id UUID NOT NULL,
                            action VARCHAR(255) NOT NULL,
                            report_id UUID,
                            created_at TIMESTAMP(6),

                            CONSTRAINT fk_audit_logs_admin_user
                                FOREIGN KEY (admin_user_id)
                                    REFERENCES users(id),

                            CONSTRAINT fk_audit_logs_report
                                FOREIGN KEY (report_id)
                                    REFERENCES reports(id)
);

CREATE TABLE password_reset_tokens (
                                       id UUID PRIMARY KEY,
                                       token VARCHAR(64) NOT NULL,
                                       user_id UUID NOT NULL,
                                       expiry_date TIMESTAMP(6) NOT NULL,
                                       used BOOLEAN NOT NULL,

                                       CONSTRAINT uk_password_reset_tokens_token
                                           UNIQUE (token),

                                       CONSTRAINT fk_password_reset_tokens_user
                                           FOREIGN KEY (user_id)
                                               REFERENCES users(id)
);

CREATE INDEX idx_reports_category_id
    ON reports(category_id);

CREATE INDEX idx_attachments_report_id
    ON attachments(report_id);

CREATE INDEX idx_status_histories_report_id
    ON status_histories(report_id);

CREATE INDEX idx_audit_logs_admin_user_id
    ON audit_logs(admin_user_id);

CREATE INDEX idx_audit_logs_report_id
    ON audit_logs(report_id);

CREATE INDEX idx_password_reset_tokens_user_id
    ON password_reset_tokens(user_id);