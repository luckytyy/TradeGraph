CREATE TABLE "TradeBalance" ("id" integer,"network" varchar,"tokenInfo" varchar,"transferCnt" int,"balance" integer, PRIMARY KEY (id));
CREATE TABLE TradeInfo (id INTEGER, "index" INTEGER, block_no VARCHAR, token VARCHAR, tokenAddr, tokenSymbol, tokenDecimals VARCHAR, time DATETIME, txid VARCHAR UNIQUE, tokenInfo VARCHAR, "from" VARCHAR, fromAlias VARCHAR, "to" VARCHAR, toAlias VARCHAR, value INTEGER, conformations INT);