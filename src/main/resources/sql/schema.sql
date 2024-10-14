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
