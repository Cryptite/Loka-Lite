package com.cryptite.lite;

import com.lokamc.accounts.BaseAccountData;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator
public class AccountData extends BaseAccountData {
    public AccountData() {
    }

    public AccountData(MongoDatabase db, AccountData data) {
        super(db, data, "players");
    }
}
