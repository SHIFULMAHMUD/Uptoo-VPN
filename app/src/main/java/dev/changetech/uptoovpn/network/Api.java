package dev.changetech.uptoovpn.network;

import dev.changetech.uptoovpn.model.Data;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("rGRCzBcL?")
    Call<Data> getServerData();

}
