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
                               "initial_date" DATE,
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
                                    "scheduled_date" BIGINT,
                                    "updated_by" VARCHAR(255),
                                    "status" VARCHAR(255),
                                    "created_at" TIMESTAMP,
                                    "updated_at" TIMESTAMP,
                                    FOREIGN KEY ("budget_item_id") REFERENCES "budget_item" ("id")
);
