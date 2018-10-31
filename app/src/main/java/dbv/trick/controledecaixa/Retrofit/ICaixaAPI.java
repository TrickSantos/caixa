package dbv.trick.controledecaixa.Retrofit;

import java.util.List;

import dbv.trick.controledecaixa.Model.Banner;
import dbv.trick.controledecaixa.Model.ChecarUsuarioResponse;
import dbv.trick.controledecaixa.Model.Usuario;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ICaixaAPI {
    @FormUrlEncoded
    @POST("checarTelefone.php")
    Call<ChecarUsuarioResponse> checarUsuarioExists(@Field("telefone") String telefone);

    @FormUrlEncoded
    @POST("registrar.php")
    Call<Usuario> registrar(@Field("nome") String nome,
                            @Field("senha") String senha,
                            @Field("telefone") String telefone,
                            @Field("email") String email);

    @FormUrlEncoded
    @POST("getuser.php")
    Call<Usuario> getUser(@Field("telefone") String telefone);

    @GET("getbanner.php")
    Observable<List<Banner>> getBanner();

}
