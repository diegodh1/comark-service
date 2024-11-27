CREATE TABLE "budget" (
                          "id" INTEGER PRIMARY KEY,
                          "actor_id" VARCHAR(255),
                          "budget_amount_from_previous_year" DOUBLE PRECISION,
                          "created_at" BIGINT,
                          "updated_at" BIGINT
);

CREATE TABLE "budget_item" (
                               "id" VARCHAR(255) PRIMARY KEY,
                               "budget_id" INTEGER,
                               "type" VARCHAR(255),
                               "name" VARCHAR(255),
                               "detail" VARCHAR(255),
                               "amount" DOUBLE PRECISION,
                               "frequency" DOUBLE PRECISION,
                               "expected_frequency_amount" DOUBLE PRECISION,
                               "initial_date" BIGINT,
                               "accounting_account" VARCHAR(255), -- Changed to VARCHAR
                               "created_at" BIGINT,
                               "updated_at" BIGINT,
                               FOREIGN KEY ("budget_id") REFERENCES "budget" ("id")
);

CREATE TABLE "budget_item_task" (
                                    "id" VARCHAR(255) PRIMARY KEY,
                                    "budget_id" INTEGER,
                                    "budget_item_id" VARCHAR(255),
                                    "name" VARCHAR(255),
                                    "details" VARCHAR(255),
                                    "actual_amount" DOUBLE PRECISION,
                                    "actual_accounting_account" VARCHAR(255), -- Changed to VARCHAR
                                    "bill_id" VARCHAR(255),
                                    "scheduled_date" BIGINT,
                                    "updated_by" VARCHAR(255),
                                    "status" VARCHAR(255),
                                    "created_at" BIGINT,
                                    "updated_at" BIGINT,
                                    FOREIGN KEY ("budget_item_id") REFERENCES "budget_item" ("id")
);

CREATE TABLE building_balance (
                                  "id" VARCHAR(255) PRIMARY KEY,
                                  apartment_number VARCHAR(255) NOT NULL,
                                  date BIGINT NOT NULL,
                                  administration_charge DOUBLE PRECISION,
                                  month_charge DOUBLE PRECISION,
                                  interest_rate DOUBLE PRECISION,
                                  interest_charge DOUBLE PRECISION,
                                  interest_balance DOUBLE PRECISION,
                                  additional_charge DOUBLE PRECISION,
                                  penalty_charge DOUBLE PRECISION,
                                  legal_charge DOUBLE PRECISION,
                                  last_balance DOUBLE PRECISION,
                                  other_charge DOUBLE PRECISION,
                                  total_to_paid DOUBLE PRECISION,
                                  discount DOUBLE PRECISION,
                                  last_paid DOUBLE PRECISION,
                                  final_charge DOUBLE PRECISION
);

CREATE TABLE pqr (
                     id VARCHAR(255) PRIMARY KEY,
                     date BIGINT NOT NULL,
                     property VARCHAR(255) NOT NULL,
                     user_name VARCHAR(255) NOT NULL,
                     type VARCHAR(50) NOT NULL,  -- Assuming PqrType is stored as a string; adjust the length if needed
                     dependency VARCHAR(255) NOT NULL,
                     assigned_to VARCHAR(255) NOT NULL,
                     description TEXT NOT NULL,
                     response TEXT,              -- Nullable field
                     response_date BIGINT,       -- Nullable field
                     response_time BIGINT        -- Nullable field
);

CREATE TABLE activity (
                          id VARCHAR(255) PRIMARY KEY,
                          origin_id VARCHAR(255) NOT NULL,
                          activity_type VARCHAR(255) NOT NULL,
                          aux_id VARCHAR(255),
                          title VARCHAR(255) NOT NULL,
                          details TEXT,
                          assigned_to VARCHAR(255) NOT NULL,
                          created_at BIGINT NOT NULL,
                          scheduled_date BIGINT NOT NULL,
                          closing_date BIGINT,
                          status VARCHAR(50) NOT NULL
);

CREATE TABLE residential_complex (
                          id VARCHAR(255) PRIMARY KEY,
                          created_at BIGINT NOT NULL,
                          updated_at BIGINT NOT NULL
);

CREATE TABLE residential_complex_administrator (
                                    id VARCHAR(255) PRIMARY KEY,
                                    residential_complex_id VARCHAR(255) NOT NULL,
                                    email VARCHAR(255) NOT NULL,
                                    is_active BOOLEAN NOT NULL,
                                    created_at BIGINT NOT NULL,
                                    updated_at BIGINT NOT NULL,
                                    FOREIGN KEY ("residential_complex_id") REFERENCES "residential_complex" ("id"),
                                    UNIQUE (residential_complex_id, email)
);

CREATE TABLE residential_complex_item (
                                          id VARCHAR(255) PRIMARY KEY,
                                          residential_complex_id VARCHAR(255) NOT NULL,
                                          name VARCHAR(255) NOT NULL,
                                          description TEXT,
                                          type VARCHAR(255) NOT NULL,
                                          building_number VARCHAR(255),
                                          parking_number VARCHAR(255),
                                          storage_room_number VARCHAR(255),
                                          rent_price DECIMAL(10, 2),
                                          capacity INT,
                                          restrictions TEXT,
                                          UNIQUE (residential_complex_id, building_number, type),
                                          FOREIGN KEY ("residential_complex_id") REFERENCES "residential_complex" ("id")
);

CREATE TABLE residential_complex_item_entity (
                                                 id VARCHAR(255) PRIMARY KEY,
                                                 residential_complex_item_id VARCHAR(255) NOT NULL,
                                                 identification_type VARCHAR(50) NOT NULL,
                                                 identification_number VARCHAR(100) NOT NULL,
                                                 type VARCHAR(50) NOT NULL,
                                                 name VARCHAR(255) NOT NULL,
                                                 last_name VARCHAR(255),
                                                 phone_number VARCHAR(20) NOT NULL,
                                                 email VARCHAR(255) NOT NULL,
                                                 created_at BIGINT NOT NULL,
                                                 updated_at BIGINT NOT NULL,
                                                 is_real_state_agency BOOLEAN NOT NULL,
                                                 is_active BOOLEAN NOT NULL,
                                                 UNIQUE (identification_type, identification_number, type),
                                                 FOREIGN KEY ("residential_complex_item_id") REFERENCES "residential_complex_item" ("id")
);

CREATE TABLE residential_complex_item_event (
                                             id VARCHAR(255) PRIMARY KEY, -- Unique identifier for the event
                                             name VARCHAR(255) NOT NULL, -- Name of the event
                                             residential_complex_id VARCHAR(255) NOT NULL,
                                             residential_complex_item_id VARCHAR(255) NOT NULL, -- ID of the associated residential complex item
                                             organizer_id VARCHAR(255) NOT NULL, -- ID of the event organizer
                                             description TEXT, -- Description of the event
                                             restrictions TEXT, -- Restrictions for the event
                                             start_date_time BIGINT NOT NULL, -- Start date and time as epoch in milliseconds
                                             end_date_time BIGINT NOT NULL, -- End date and time as epoch in milliseconds
                                             event_status VARCHAR(50) NOT NULL, -- Status of the event (assuming enum as a string),
                                             created_at BIGINT NOT NULL,
                                             updated_at BIGINT NOT NULL,
                                             FOREIGN KEY ("residential_complex_item_id") REFERENCES "residential_complex_item" ("id"),
                                             FOREIGN KEY ("residential_complex_id") REFERENCES "residential_complex" ("id")
);
