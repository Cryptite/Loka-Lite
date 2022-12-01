package com.cryptite.lite;

import com.lokamc.accounts.BaseAccountData;
import com.lokamc.db.LokaDB;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator
public class AccountData extends BaseAccountData {
    public AccountData() {
    }

    public AccountData(LokaDB db, AccountData data) {
        super(db, data, "players");
    }
}
