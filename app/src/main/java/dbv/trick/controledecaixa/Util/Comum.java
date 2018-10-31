package dbv.trick.controledecaixa.Util;

import dbv.trick.controledecaixa.Model.Usuario;
import dbv.trick.controledecaixa.Retrofit.ICaixaAPI;
import dbv.trick.controledecaixa.Retrofit.RetrofitClient;

/**
 * Created by admin on 23/10/2018.
 */

public class Comum {
    private static final String BASE_URL = "http://192.168.1.4/caixa2/";
    public static Usuario currentUser = null;

    public static ICaixaAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(ICaixaAPI.class);
    }
}
